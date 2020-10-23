package implement;

import my_interface.Block;
import my_interface.BlockManager;

import java.io.*;
import java.nio.ByteBuffer;
import java.security.MessageDigest;

import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class BlockManagerImpl implements BlockManager
{
    //使用单例模式，保证全局只有一个块管理器对象
    private static BlockManagerImpl blockManager=new BlockManagerImpl();
    private BlockManagerImpl(){}
    public static BlockManagerImpl getBlockManager(){
        return blockManager;
    }
    public BlockManagerImpl(String bm){
        File directory=new File("src/"+bm);
        if(!directory.exists()){
            directory.mkdir();
        }else{
            System.out.println("块管理器已存在，请重新输入");
        }
    }
    private int blockSize=3;
    public BlockImpl getBlock(String BM, int blockNumber)
    {
        File file = new File("src/"+BM + "/" + blockNumber + ".meta");
        BlockImpl block = null;
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file)))
        {
            block = (BlockImpl) objectInputStream.readObject();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("blockManager获取块失败");
        }
        return block;
    }


    @Override
    public Block getBlock(my_interface.Id indexId)
    {
        return getBlock(indexId.getManager(),Integer.parseInt(indexId.getId()));
    }

    @Override
    public Block newBlock(byte[] b)
    {
         String[][] blockList= allocateBlock(b);
         return getBlock(blockList[0][0],Integer.parseInt(blockList[0][1]));
    }

    /**
     * 创建并保存一个新的块
     * @param bytes 要保存的内容
     * @param bm 属于第几个blockManager
     * @param blockNum 块号
     * @return
     */
    public BlockImpl newBlock(byte[] bytes,int bm,int blockNum){
        String[] bms={"BM1","BM2","BM3"};
        long checksum=getChecksum(bytes);
        BlockImpl block=new BlockImpl(bms[bm],blockNum,checksum,bytes.length);
        block.saveBlock(bytes);
        return block;
    }

    public long getChecksum(byte[] bytes){
        byte[] bytes1=null;
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            md.update(bytes);
            bytes1=md.digest();
        } catch ( NoSuchAlgorithmException e) {
            System.out.println(e.getMessage());
            System.out.println("MD5加密出现错误");
        }
        return byteArrayToLong(bytes1);
    }
    public long byteArrayToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();
        return buffer.getLong();
    }

    /**
     * 为数据分配多个块
     */
    public String[][]  allocateBlock(byte[] content) throws ErrorCodeException
    {
        //获得记录三个块管理器中的最大文件id数量
        int[] maxBlockNum = new int[3];
        //该文件需要的块数量
        int num=content.length%blockSize==0?content.length/blockSize:(content.length/blockSize)+1;
        //第一维存BM，第二维存块号，偶数位都是原本，奇数位都是副本
        String[][] blockInFile=new String[num*2][2];
        File file = new File("src/bm.txt");
        try (DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(file))))
        {
            for (int i=0;i<maxBlockNum.length;i++){
                maxBlockNum[i]=dataInputStream.readInt();
            }
            //按每个块的大小划分出对应的byte，并将其填充到新块当中
            Random random=new Random();
            for(int i=0;i<num;i++){
                //每次存一个原文件块和一个副本块
                byte[] tmpBytes=spiltByte(content,i,num);

                //先存第一个块
                int tmp=random.nextInt(3);
                BlockImpl block1=newBlock(tmpBytes,tmp,++maxBlockNum[tmp]);
                blockInFile[2*i][0]=block1.getBM();
                blockInFile[2*i][1]=block1.getBlockNumber()+"";

                //保存第一个副本块
                tmp=random.nextInt(3);
                BlockImpl block2=newBlock(tmpBytes,tmp,++maxBlockNum[tmp]);
                blockInFile[2*i+1][0]=block2.getBM();
                blockInFile[2*i+1][1]=block2.getBlockNumber()+"";
            }

            //将新的最大文件id数量写回文件
            DataOutputStream dataOutputStream=new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
            for(int i=0;i<maxBlockNum.length;i++){
                dataOutputStream.writeInt(maxBlockNum[i]);
            }
            dataOutputStream.close();

        }
        catch (IOException e)
        {
            System.out.println("分配新块失败");
            throw  new ErrorCodeException(ErrorCodeException.ALLOCATE_NEW_BLOCK_FAILED);
        }
        return blockInFile;
    }
    private byte[] spiltByte(byte[] bytes,int index,int num){
        byte[] newBytes;
        if (index+1==num){
            //如果是最后一块，则取end-index*blockSize长度
            newBytes=new byte[bytes.length-index*blockSize];
            for(int i=0;i<newBytes.length;i++){
                newBytes[i]=bytes[ i+ ( index*blockSize ) ];
            }
        }else{//如果是正常的块，则取blockSize长度
            newBytes=new byte[blockSize];
            for(int i=0;i<newBytes.length;i++){
                newBytes[i]=bytes[index*blockSize+i];
            }
        }
        return newBytes;
    }

    public int getBlockSize()
    {
        return blockSize;
    }
}

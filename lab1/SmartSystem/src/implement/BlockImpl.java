package implement;

import my_interface.Block;
import my_interface.BlockManager;

import java.io.*;



public class BlockImpl implements Block,Serializable
{
    private static final long serialVersionUID =-8781362791982299870L;
    //文件内容的实际大小
    private int actualSize;
    private  int blockSize;
    private String BM;
    private int blockNumber;
    //检验和
    private long checksum;


    public BlockImpl( String BM, int blockNumber, long checksum,int actualSize)
    {
        this.BM = BM;
        this.blockNumber = blockNumber;
        this.checksum = checksum;
        this.blockSize=3;
        this.actualSize=actualSize;
    }

    public int getBlockSize()
    {
        return blockSize;
    }

    @Override
    public Id getIndexId()
    {
        return new Id(BM,blockNumber+"");
    }

    @Override
    public BlockManager getBlockManager()
    {
        return BlockManagerImpl.getBlockManager();
    }
    public String getBM(){return BM;}

    @Override
    public byte[] read()
    {
        BufferBlockManager bufferBlockManager=BufferBlockManager.getBufferBlockManager();
        byte[] data=bufferBlockManager.get(BM+blockNumber);
        //如果缓存中没有数据，则从文件中读
        if (data==null){
            File file=new File(getPath("data"));
            try(BufferedInputStream bufferedInputStream=new BufferedInputStream(new FileInputStream(file))){
                byte[] bytes=new byte[actualSize];
                bufferedInputStream.read(bytes);
                bufferBlockManager.put(BM+blockNumber,bytes);
                return bytes;
            }catch (Exception e){
                System.out.println(e.getMessage());
                System.out.println("读取块错误");
            }
        }
        //否则返回数据
        return data;
    }

    private String getPath(String type){
        return "src/"+BM+"/"+blockNumber+"."+type;
    }

    @Override
    public int blockSize()
    {
        return blockSize;
    }



    public int getBlockNumber()
    {
        return blockNumber;
    }

    public long getChecksum()
    {
        return checksum;
    }

    public int getActualSize()
    {
        return actualSize;
    }
    public void saveBlock(byte[] bytes){
        saveBlockData(bytes);
        saveBlockMeta();
    }
    public void saveBlockMeta(){
        File file=new File(getPath("meta"));
        try
        {
            if(!file.exists()){
                file.createNewFile();
            }
            ObjectOutputStream objectOutputStream=
                    new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
            objectOutputStream.writeObject(this);
            objectOutputStream.close();
        }catch (IOException e){
            System.out.println(e.getMessage());
            System.out.println("保存块meta错误");
        }

    }
    public void saveBlockData(byte[] bytes){
        File file=new File(getPath("data"));

        try(BufferedOutputStream bufferedOutputStream=new BufferedOutputStream(new FileOutputStream(file))){
            bufferedOutputStream.write(bytes);
        }catch (Exception e){
            System.out.println(e.getMessage());
            System.out.println("保存块data错误");
        }
    }



}

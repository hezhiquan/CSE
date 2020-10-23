import implement.FileImpl;
import implement.FileManagerImpl;
import implement.Id;

import java.io.*;
public class test
{
    public static void main(String[] args){
//        byte tmp=new Byte("00");
//        String str="0";
//        byte[] b2=str.getBytes();
//        System.out.println(b2[0]);
//        clear();
//        readBm();
        String fm="FM1";
        String filename="file1";
        FileManagerImpl fileManager=FileManagerImpl.getFileManager();
//        Id id=new Id("FM2","file2");
//        FileImpl file=(FileImpl) fileManager.getFile(id);
//        System.out.println(file.getFileId().getManager()+" | "+file.getFileId().getId());
//        getFile(fm,filename);
        getFile("FM2","file2");
        getFile("FM1","file1");

    }
    public static void getFile(String fm,String  filename){
        File file=new File("src/"+fm+"/"+filename+".meta");
        try(ObjectInputStream objectInputStream=new ObjectInputStream(new BufferedInputStream(new FileInputStream(file))))
        {
            FileImpl file1=(FileImpl) objectInputStream.readObject();
            file1.setCursor(0);
            System.out.println(file1.getFM()+" "+file1.getFilename());
            System.out.println(file1.getFileSize()+" 文件大小 ");
            System.out.println(file1.getBlockSize()+" 块的大小");
            String[][] bl=file1.getBlockLists();
            for (int i = 0; i < bl.length; i++)
            {
                System.out.println(bl[i][0]+" | "+bl[i][1]);
            }
            System.out.println(new String(file1.getFileData()));
        }catch (Exception e){
            System.out.println(e.getMessage());
            System.out.println("获取文件失败");
        }
    }
    private static void clear(){
        writeBM();
        readBm();
        File file1=new File("src/BM1");
        File file2=new File("src/BM2");
        File file3=new File("src/BM3");
        File file4=new File("src/FM1");
        File file5=new File("src/FM2");
        File file6=new File("src/FM3");
        deleteFile(file1);
        deleteFile(file2);
        deleteFile(file3);
        deleteFile(file4);
        deleteFile(file5);
        deleteFile(file6);
    }
    private static void deleteFile(File file){
        if (file.isFile()){//判断是否为文件，是，则删除
            System.out.println(file.getAbsoluteFile());//打印路径
            file.delete();
        }else{//不为文件，则为文件夹
            String[] childFilePath = file.list();
            //获取文件夹下所有文件相对路径
            for (String path:childFilePath){
                File childFile= new File(file.getAbsoluteFile()+"/"+path);
                childFile.delete();
            }
            System.out.println(file.getAbsoluteFile());

        }
    }

    public static String bytes2hex03(byte[] bytes)
    {
        final String HEX = "0123456789abcdef";
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes)
        {
            // 取出这个字节的高4位，然后与0x0f与运算，得到一个0-15之间的数据，通过HEX.charAt(0-15)即为16进制数
            sb.append(HEX.charAt((b >> 4) & 0x0f));
            // 取出这个字节的低位，与0x0f与运算，得到一个0-15之间的数据，通过HEX.charAt(0-15)即为16进制数
            sb.append(HEX.charAt(b & 0x0f));
        }

        return sb.toString();
    }

    private static byte[] spiltByte(byte[] bytes,int index,int num){
        int blockSize=5;
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
    private static  void writeBM(){
        File file=new File("src/bm.txt");
        try{
            DataOutputStream da=new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
            da.writeInt(0);
            da.writeInt(0);
            da.writeInt(0);
            da.close();
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }

    }
    private static void readBm(){
        File file=new File("src/bm.txt");
        try{
            DataInputStream da=new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
            System.out.println(da.readInt());
            System.out.println(da.readInt());
            System.out.println(da.readInt());
            da.close();
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

}

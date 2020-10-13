import implement.*;
import java.io.*;

import java.util.Scanner;

public class Main
{

    private static FileManagerImpl fileManager=FileManagerImpl.getFileManager();
    public static void main(String[] args){
        System.out.println("-----------welcome to smartFileSystem------------");
        System.out.println("cat fm filename ");
        System.out.println("hex bm blockName");
        System.out.println("wri fm filename index");
        System.out.println("copy fm fromFile fm toFile");
        System.out.println("sce fm filename");
        Scanner input=new Scanner(System.in);
//        try
//        {
            while (true){
                String command=input.nextLine();
                String[] strArr=command.split(" ");
                if(command.startsWith("cat")&&strArr.length==3){
                    String[] tmp;
                    if (checkPath(strArr[1],strArr[2])){
                        while (true){
                            System.out.println("example: move offset c|h|t");
                            command=input.nextLine();
                            tmp=command.split(" ");

                            if (command.startsWith("move")&&tmp.length==3){

                                break;
                            }else {
                                System.out.println("move格式错误");
                            }
                        }
                        smartCat(strArr[1],strArr[2],tmp[1],tmp[2]);
                    }
                }
                else if(command.startsWith("hex")&&strArr.length==3){
                    if (checkPath(strArr[1],strArr[2])){
                        smartHex(strArr[1],Integer.parseInt(strArr[2]));
                    }
                }
                else if(command.startsWith("wri")&&strArr.length==4){
                    writeToFile(input, strArr);
                }
                else if(command.startsWith("copy")&&strArr.length==5){
                    if (checkPath(strArr[1],strArr[2])){
                        smartCopy(strArr[1],strArr[2],strArr[3],strArr[4]);
                    }

                }
                else if(command.startsWith("sce")&&command.split(" ").length==3){
                    smartCreate(strArr[1],strArr[2]);
                }
                else if("exit".equals(command)){
                    break;
                }
                else{
                    System.out.println("wrong input! ");
                }
            }
//        }
//        catch (ErrorCodeException e){
//            System.out.println(e.getMessage());
//            System.out.println(e.getStackTrace());
//        }

        //命令行测试
//        smartCreate(String FM,String filename)
//        smartCat(String FM,String filename,String offset,String where)
//        smartHex(String BM,int blockNum )
//        smartWrite (String FM,String filename,long index,String content,String offset,String where)
//        smartCopy(String fm1,String fromFile,String fm2,String toFile)

//        String fm="FM1";
//        String filename="f1";
//        String content="12345678901234567890\n" + "23456789012345678901\n" + "34567890123456789012\n";
//        String offset="0";
//        String where="c";
//        long index=0;
//        smartCreate(fm,filename);
//        smartWrite (fm,filename,index,content,offset,where);

    }

    private static void writeToFile(Scanner input,String[] strArr)
    {
        if(checkPath(strArr[1], strArr[2])){
            String[] tmp2;
            while (true){
                System.out.println("example: move offset c|h|t");
               String command2= input.nextLine();
                tmp2=command2.split(" ");
                if (command2.startsWith("move")&&tmp2.length==3){
                    break;
                }else {
                    System.out.println("move格式错误");
                }
            }
            System.out.println("please input written data");
            StringBuilder content=new StringBuilder();
            while (true){
                String tmp= input.nextLine();
                if("!please!quit!".equals(tmp)) {
                    break;
                }
                content.append(tmp+"\n");
            }
            smartWrite(strArr[1], strArr[2],Long.parseLong(strArr[3]),new String(content),tmp2[1],tmp2[2]);
        }
    }

    /**
     * 检查是否有对应的文件与路径相对应，如果有，返回true，没有返回false
     * @param manager 文件管理器或者块管理器的名字
     * @param file 文件名或者块号
     * @return
     */
    private static boolean checkPath(String manager,String file){
        File directory=new File("src/"+manager);
        File file1=new File("src/"+manager+"/"+file+".meta");
        if(directory.exists()&&directory.isDirectory()&&file1.exists()){
            return true;
        }else{
            System.out.println("路径输入错误，请重新输入");
            return false;
        }
    }
    public static void smartCat(String FM,String filename,String offset,String where){

        FileImpl file=fileManager.getFileImpl(FM,filename);
        System.out.println("move之前光标位置 "+file.getCursor());
        file.move(Long.parseLong(offset),getWhereNum(where));
        System.out.println("move后，读数据之前光标位置 "+file.getCursor());
        String data=new String(file.getFileData());
        //读完后将光标置尾
        file.setCursor(file.getFileSize()-1);
        System.out.println("读数据之后之后光标位置 "+file.getCursor());
        System.out.println(data);
    }
    public static  void smartHex(String BM,int blockNum ){
        BlockManagerImpl blockManager=BlockManagerImpl.getBlockManager();
        BlockImpl block=blockManager.getBlock(BM,blockNum);
        byte[] content=block.read();
        if (block.getChecksum()!=blockManager.getChecksum(content)){
            System.out.println("该块已损坏，正在查看已损坏的内容");
        }
        System.out.println(bytes2hex(content));
    }
    public static  void smartWrite (String fm, String filename, long index, String content, String offset, String where) {
        long offset2=Long.parseLong(offset);
        int where2=getWhereNum(where);
        FileImpl file=fileManager.getFileImpl(fm,filename);
        if(file!=null){
            if (file.size()==0){
                //第一次写入文件
                byte[] bytes=content.getBytes();
                file.setCursor(bytes.length);

                file.write(bytes);
                System.out.println("文件写入成功");
            }else if(index<file.size()&& index>=0){
                //实现插入
                file.setCursor(index);
                file.move(offset2,where2);
                file.insert(content.getBytes());
                System.out.println("文件写入成功.");
            }else{
                System.out.println("index输入错误");
            }
        }
    }
    public static void smartCopy(String fm1,String fromFile,String fm2,String toFile){
        if(fm1.equals(fm2)&&fromFile.equals(toFile)){
            //如果是同一个文件自身复制，不处理
            System.out.println("文件自复制");
            return;
        }
        File file1=new File("src/"+fm1+"/"+fromFile+".meta");
        File directory=new File("src/"+fm2);
        if(directory.exists()&&directory.isDirectory()){
            File file2=new File("src/"+fm2+"/"+toFile+".meta");
            try
            {
                if (!file2.exists()){
                    file2.createNewFile();
                }
                ObjectInputStream objectInputStream=
                        new ObjectInputStream(new BufferedInputStream(new FileInputStream(file1)));
                FileImpl oldFile=(FileImpl) objectInputStream.readObject();
                oldFile.setFileManager(fm2);
                oldFile.setFileId(toFile);
                ObjectOutputStream objectOutputStream=
                        new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file2)));
                objectOutputStream.writeObject(oldFile);
                objectOutputStream.close();
                objectInputStream.close();
                System.out.println("copy成功");
            }catch (Exception e){
                System.out.println(e.getMessage());
                System.out.println("复制文件失败");
            }

        }
        else {
            //FM 不可用
            throw new ErrorCodeException(ErrorCodeException.FM_UNAVAILABLE);
        }

    }
    public static  void smartCreate(String fm, String filename){
        File directory=new File("src/"+ fm);
        if(directory.exists()&&directory.isDirectory()){
            File file=new File("src/"+ fm +"/"+filename+".meta");
            try
            {
                if(!file.exists()){
                    file.createNewFile();
                    ObjectOutputStream objectOutputStream=
                            new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
                    FileImpl file1=new FileImpl(fm,filename,0);
                    objectOutputStream.writeObject(file1);
                    objectOutputStream.close();
                }
                else{
                    throw new ErrorCodeException(ErrorCodeException.FILENAME_DUPLICATION);
                }
            }
            catch (IOException e){
                System.out.println(e.getMessage());
                System.out.println("创建文件失败");
            }
        }

        System.out.println("文件创建成功");
    }

    private static String bytes2hex(byte[] bytes)
    {
        final String hex = "0123456789abcdef";
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes)
        {
            // 取出这个字节的高4位，然后与0x0f与运算，得到一个0-15之间的数据，通过HEX.charAt(0-15)即为16进制数
            sb.append(hex.charAt((b >> 4) & 0x0f));
            // 取出这个字节的低位，与0x0f与运算，得到一个0-15之间的数据，通过HEX.charAt(0-15)即为16进制数
            sb.append(hex.charAt(b & 0x0f));
        }

        return sb.toString();
    }
    private static int getWhereNum(String where){
        switch (where){
            case "c":return 0;
            case "h":return 1;
            case "t":return 2;
            default:throw new ErrorCodeException(ErrorCodeException.WRONG_WHERE);
        }
    }

}

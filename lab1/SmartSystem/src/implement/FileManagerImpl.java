package implement;


import my_interface.FileManager;

import java.io.*;
import java.util.ArrayList;

public class FileManagerImpl implements FileManager
{
    private  ArrayList<FileImpl> fileList;

    private static FileManagerImpl fileManager=new FileManagerImpl();
    public static FileManagerImpl getFileManager(){
        return fileManager;
    }
    public FileManagerImpl(String fm){
        File directory=new File("src/"+fm);
        if(!directory.exists()){
            directory.mkdir();
        }else{
            System.out.println("文件管理器已存在，请重新输入");
        }

    }
    private FileManagerImpl()
    {
        this.fileList = new ArrayList<>();
    }


    public FileImpl getFileImpl(String fm,String filename) {
        int index=indexOfList(fm,filename);
        if(index!=-1){
            return fileList.get(index);
        }
        else{
            File file=new File("src/"+fm+"/"+filename+".meta");
            try(ObjectInputStream objectInputStream=new ObjectInputStream(new BufferedInputStream(new FileInputStream(file))))
            {
                FileImpl file1=(FileImpl) objectInputStream.readObject();
                fileList.add(file1);
                //将当前光标设为0
                file1.setCursor(0);
                return file1;
            }catch (Exception e){
                System.out.println(e.getMessage());
                System.out.println("获取文件失败");
            }
        }
        return null;
    }

    /**
     * 查找文件实例是否在文件列表中，不在返回-1，在则返回对应的下标
     * @param fm
     * @param filename
     * @return
     */
    private int indexOfList(String fm,String filename){
        for (int i = 0; i < fileList.size(); i++)
        {
            if(fileList.get(i).getFM().equals(fm)&&fileList.get(i).getFilename().equals(filename)){
                return i;
            }
        }
        return -1;
    }



    /**
     * 对已经存在的文件写入数据，并将其保存到file.meta中
     * @param fm
     * @param filename
     * @param content
     * @return
     */
    public FileImpl newFileImpl(String fm,String filename,byte[] content){

        FileImpl fileImpl=getFileImpl(fm,filename);
        fileImpl.write(content);
        System.out.println("新文件写入完毕");
        return fileImpl;
    }


    @Override
    public my_interface.File getFile(my_interface.Id fileId)
    {
        return getFileImpl(fileId.getManager(),fileId.getId());
    }

    @Override
    public my_interface.File newFile(my_interface.Id fileId)
    {
        return newFileImpl(fileId.getManager(),fileId.getId(),new byte[0]);
    }
}

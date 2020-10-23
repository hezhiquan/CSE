package implement;

import java.util.LinkedHashMap;
import java.util.Map;

public class BufferBlockManager
{
    /**
     * buffer中的总共有20个块
     */
    final int capacity =20;
    private LinkedHashMap<String,byte[]> linkedHashMap;
    private static BufferBlockManager bufferBlockManager=new BufferBlockManager();
    public static BufferBlockManager getBufferBlockManager(){
        return bufferBlockManager;
    }
    private BufferBlockManager(){
        linkedHashMap=new LinkedHashMap<String, byte[]>(capacity,0.75f,true){
            @Override
            protected boolean removeEldestEntry(Map.Entry eldest){
                    return size()>capacity;
            }
        };
    }
    public byte[] get(String bmAndBlockNum){
        return linkedHashMap.getOrDefault(bmAndBlockNum,null);
    }
    public void put(String bmAndBlockNum,byte[] data){
        linkedHashMap.put(bmAndBlockNum,data);
    }
    public void clear(){
        //清空所有的文件缓存
        if (linkedHashMap.size()!=0){
            System.out.println("缓存已清空");
            linkedHashMap.clear();
        }
    }
}

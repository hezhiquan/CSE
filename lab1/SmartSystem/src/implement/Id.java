package implement;

public class Id implements my_interface.Id
{
    private String manager;
    private String id;

    public Id(String manager, String id)
    {
        this.manager = manager;
        this.id = id;
    }
    public String getPath(String type){
        return "src/"+manager+"/"+id+"."+type;
    }

    public String getManager()
    {
        return manager;
    }

    public String getId()
    {
        return id;
    }
}

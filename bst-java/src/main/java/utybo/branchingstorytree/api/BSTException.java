package utybo.branchingstorytree.api;

public class BSTException extends Exception
{
    private int where;
    
    public BSTException(int where)
    {
        super();
        this.where = where;
    }

    public BSTException(int where, String message, Throwable cause)
    {
        super(message, cause);
        this.where = where;
    }

    public BSTException(int where, String message)
    {
        super(message);
        this.where = where;
    }

    public BSTException(int where, Throwable cause)
    {
        super(cause);
        this.where = where;
    }

    public int getWhere()
    {
        return where;
    }

    public void setWhere(int where)
    {
        this.where = where;
    }
    
    
}

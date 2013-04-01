package com.blakava.datastructure;

public class Node{   
    public int [ ]next = new int[ 10 ];
    public int fail;
    public int parent;
    public boolean isEnd;
    
    public Node( int parent ){
        fail = -1;
        this.parent = parent;
        isEnd = false;
    }
}
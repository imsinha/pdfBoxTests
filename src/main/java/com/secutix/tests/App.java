package com.secutix.tests;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        //PdfCreator.getInstance().createPDf();
        long startime = System.currentTimeMillis();
        PdfBoxExample.getInstance().createPage();
        long endTime = System.currentTimeMillis();
        System.out.println("Time(ms) : "+(endTime-startime));
    }
}

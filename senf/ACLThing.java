/*
 *
 * Senf was created by the Information Security Office
 * at the Univeristy of Texas at Austin.
 *
 * This work is licensed under the Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 United States
 * License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by-nc-sa/3.0/us/
 *
 * Send comments to security@utexas.edu
 *
 */

package senf;

import java.io.*;
import javax.swing.JTable;
import java.util.ArrayList;

public class ACLThing
{
	private final String ACL_CONFIG_FILE = "senf.acl";
	private File ACLConfig;
	private String[] lines;
	private ArrayList< String > linesList;
    private boolean exists = true;

	public ACLThing()
	{
		try
		{
			ACLConfig = new File( ACL_CONFIG_FILE );
			BufferedReader ACLReader = new BufferedReader( new FileReader( ACLConfig ) );
			String line;
			linesList = new ArrayList< String >();

                        while( ( line=ACLReader.readLine() )!= null )
                        {
				linesList.add( line );
                        }

                        ACLReader.close();
                }
		catch ( java.io.FileNotFoundException fnfe )
		{
                        exists = false;
                        System.out.println( "ACL not found!" );
                }
		catch ( java.io.IOException ioe )
		{
                        System.out.println( "Error reading ACL file!" );
                }
	}

	public void add( String toAdd )
	{
        if( exists )
            linesList.add( toAdd );
        else
        {
            createACL();
        }
	}

	public void remove( int toRemove )
	{
        if( exists )
            linesList.remove( toRemove );
	}

	public void swap( int one, int two )
	{
        if( exists )
        {
            String first = linesList.get( one );
            String second = linesList.get( two );
            linesList.set( one, second );
            linesList.set( two, first );
        }
	}

	public int getSize()
	{
        if( !exists )
            return 0;

		return linesList.size();
	}

	public String[] getLines()
	{
        if( !exists )
            return new String[0];

		String[] lines = new String[ linesList.size() ];
		int i;

		for( i=0; i<linesList.size(); i++)
		{
			lines[i] = linesList.get( i );
		}

		return lines;
	}

	public void finalize( JTable ACLTable )
	{
        if( !exists )
            createACL();
        
            try
            {
                BufferedWriter ACLWriter = new BufferedWriter( new FileWriter( ACLConfig ) );
                int i;

                for( i=0; i<linesList.size(); i++ )
                {
                    ACLWriter.write( linesList.get( i )+"\n" );
                }

                ACLWriter.close();
            }
            catch ( java.io.FileNotFoundException fnfe )
            {
                    System.out.println( "ACL not found!" );
            }
            catch ( java.io.IOException ioe )
            {
                    System.out.println( "Error reading ACL file!" );
            }
	}

    public void createACL()
    {
        try
        {
            File acl = ACLConfig;
            if (!acl.exists() )
            {
                acl.createNewFile();
            }

            exists = true;
        }
        catch( Exception E )
        {
            System.out.println( "Error Creating ACL!" );
        }
    }
}


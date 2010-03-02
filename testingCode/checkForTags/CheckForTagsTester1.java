/**
 * The CheckForTagsTester1 class is a sample source code file used to test the
 * functionality of the CheckForTags analysis module. Various methods are
 * provided to specifically test the correct identification of a tag, or
 * to validate that the analysis module recognizes that the tag is not
 * present.
 *
 * The CheckForTags module ensures the presence of all @param tags in methods
 * where there are formal parameters, @throws in methods that state that they
 * throw an exception (in the method header), and @return in methods that
 * return a value or reference.
 */
public class CheckForTagsTester1
{
 	//========================
 	// Start @return testing
	//========================
    /**
     * This method takes no parameters, throws nothing, and returns nothing, so
     * therefore it requires no Javadoc @param, @throws, or @return tags. The
     * analysis module performs no other checks on other tags, and should not
     * flag these comments as requiring the appropriate tags.
     * Expected Result: CORRECT
     */
    public void returnTest1()
    {
        int i = 1;
    }
    
    
    /**
     * This method takes no parameters, throws nothing, and returns nothing, so
     * therefore it requires no Javadoc @param, @throws, or @return tags.
     * There is a @return tag present however, and it should not be there.
     * Expected Result: PARTIALLY CORRECT 
     * 
     * @return something that is not returned
     */
    public void returnTest2()
    {
        int i = 1;
    }
    
    
    /**
     * This method takes no parameters, throws nothing, and returns a boolean
     * value. Thus, the @return tag is required, is present, but is lacking a 
     * comment following the tag.
     * Expected Result: PARTIALLY CORRECT
     *
     * @return
     */
    public boolean returnTest3()
    {
        return true;
    }
    
    
    /**
     * This method takes no parameters, throws nothing, and returns a boolean
     * value. Thus, the @return tag is required, is present, and should not be
     * flagged by this module as missing.
     * Expected Result: CORRECT
     *
     * @return  this method always returns a true value.
     */
    public boolean returnTest4()
    {
        return true;
    }
    
    
    /**
     * This method takes no parameters, throws nothing, and returns a boolean
     * value. Thus, the @return tag is required, missing, and should be
     * flagged by this module as such.
     * Expected Result: INCORRECT
     */
    public boolean returnTest5()
    {
        return true;
    }
    
    /**
     * This method takes no parameters, throws nothing, and returns a boolean
     * value. Thus, the @return tag is required. It is provided, however, there
     * are multiple @return tags, and should be flagged as such.
     * Expected Result: INCORRECT
     *
     * @return	this method always returns a true value.
     * @return	this method always returns a false value.
     */
    public boolean returnTest6()
    {
    	return true;
    }
	//========================

	//========================
	// Start @param testing    
	//========================
    /**
     * The @param tag is associated with the parameter correctly (by name), but
     * is missing a comment. 
     * Expected Result: INCORRECT
     *
     * @param	alpha
     */
    public void paramTest1(int alpha)
    {
    	int i = 1;
    }


    /**
     * The @param tag is associated with the parameter correctly (by name), and
     * contains a comment, so we expect no issues.
     * Expected Result: CORRECT
     *
     * @param	alpha	the alpha parameter
     */
    public void paramTest2(int alpha)
    {
    	int i = 1;
    }


    /**
     * The @param tag for the 'alpha' tag is missing.
     * Expected Result: INCORRECT
     */
    public void paramTest3(int alpha)
    {
    	int i = 1;
    }


    /**
     * The @param tag for the 'beta' tag matches no formal parameter.
     * Expected Result: INCORRECT
     *
     * @param	beta	the beta parameter
     */
    public void paramTest4()
    {
    	int i = 1;
    }
	//========================

 	//========================
 	// Start @throws testing
	//========================
    /**
     * The @throws tag is associated with the exception correctly (by type),
     * but is missing a comment. 
     * Expected Result: INCORRECT
     *
     * @throws	Exception
     */
    public void throwsTest1() throws Exception
    {
        throw new Exception();
    }


   /**
     * The @throws tag is associated with the exception correctly (by type),
     * and a comment is present.
     * Expected Result: CORRECT
     *
     * @throws Exception A basic exception object.
     */
    public void throwsTest2() throws Exception
    {
        throw new Exception();
    }

    /**
     * This method takes no parameters, returns nothing, but throws a basic
     * Exception object. Thus, the @throws tag is required, missing, and should
     * be flagged by this module as such.
     */
    public void throwsExceptionMissing() throws Exception
    {
        // Perform no operation
    }
}
/*******************************************************************************
 * $Id$
 * $Author$
 * $Date$
 *
 * Copyright 2002 - YAJUL Developers, Joshua Davis, Kent Vogel.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 ******************************************************************************/

/*********************************************************************************
 * Old log...
 *
 *      Revision 1.3  2001/04/04 14:27:14  kvogel
 *      Made sure getISOXXX methods return a date in GMT time
 *
 *      Revision 1.2  2001/02/23 19:55:14  kvogel
 *      Tool that signs, and authenticates XML.
 *
 *      Revision 1.1  2000/12/01 23:55:57  cvsuser
 *      Moved to com.rzzzzzzz.util
 *      date	2000.10.18.21.22.00;	author joshuad;	state Exp;
 *
 * 1     10/18/00 5:22p Joshuad
 * Moved to com.rzzzzzzz.util
 **********************************************************************************/

package org.yajul.util;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * Parses command line arguments, with POS*X-like syntax.
 * @author Kent Vogel
 * @author Joshua Davis
 */
public class CommandLine
{

    /** The default option prefix characters (- and +) **/
    public static final String DEFAULT_OPTION_PREFIX = "-+";

    /** The original command line arguments. **/
    private String[] args;

    /** True if the original arguments have been parsed. **/
    private boolean parsed;

    /** The non-option arguments. **/
    private Argument[] arguments;

    /** The optional arguments. **/
    private Option[] options;

    /** The option prefix character. **/
    private String optionPrefix = DEFAULT_OPTION_PREFIX;

    /** A map of (String{name}->OptionDefinition). **/
    private Map optionDefinitions = new HashMap();

    /**
     * Constructs a new CommandLine fot given array of arguments.
     * @param args Command line arguments (typically from a main() method).
     */
    public CommandLine(String[] args)
    {
        this.args = args;
        parsed = false;
    }

    /**
     * Defines a new toggle (on/off) option.   Once this method has been
     * called, the command line parser will interpret
     *  <code>{prefix}{name}</code> as a toggle option, whos value can
     * be
     * @param name The name of the command line option.
     */
    public void defineOption(String name)
    {
        OptionDefinition o = new OptionDefinition(name);
        optionDefinitions.put(name, o);
    }

    /**
     * Returns the number of arguments that are *not* optionDefinitions.
     * @return int - The number of arguments that are not optionDefinitions.
     * @throws CommandLineException if there is a problem getting the argument.
     */
    public int getArgumentCount() throws CommandLineException
    {
        parse();
        return arguments.length;
    }

    /**
     * Returns the number of optional arguments specified.
     * @return int - The number of optional arguments.
     */
    public int getOptionCount() throws CommandLineException
    {
        parse();
        return options.length;
    }

    /**
     * Return the n'th non-option command line argument.
     * @param index The index.
     * @return String - The n'th non-option command line argument.
     * @throws CommandLineException if there is a problem getting the argument.
     */
    public String get(int index)
            throws CommandLineException
    {
        parse();
        if (index < 0)
            throw new CommandLineException("Argument index cannot be < 0!");
        return arguments[index].getValue();
    }

    /**
     * Returns true if the option has been specified at least once on the
     * command line.
     * @param name The name of the option (must be defined first).
     * @return boolean - True if the option was speicifed, false if not.
     * @throws CommandLineException if the option has not been defined.
     */
    public boolean isSpecified(String name)
        throws CommandLineException
    {
        parse();

        // Find the option by name.
        OptionDefinition o = (OptionDefinition) optionDefinitions.get(name);

        if (o == null)
            throw new CommandLineException("Unknown option: '" + name + "'");

        return o.getInstanceCount() > 0;
    }

    private void parse() throws CommandLineException
    {
        if (parsed)
            return;

        char[] optionPrefixChar = optionPrefix.toCharArray();
        ArrayList argumentList = new ArrayList();
        ArrayList optionList = new ArrayList();

        for (int i = 0; i < args.length; i++)
        {
            String arg = args[i];
            char first = arg.charAt(0);
            boolean isOption = false;

            // Compare the first character to the prefix characters...
            for (int j = 0; j < optionPrefixChar.length; j++)
            {
                char c = optionPrefixChar[j];
                if (first == c)
                {
                    // It's an option!
                    isOption = true;
                    // Process the option as an argument, incrementing
                    // the current arg counter if necessary.
                    i += processOption(first, args, i, optionList);
                }
            } // for j

            // Accumulate non-option arguments in the argument list.
            if (!isOption)
            {
                argumentList.add(new Argument(i, argumentList.size()));
            }
        } // for i

        arguments = (Argument[]) argumentList.toArray(
                new Argument[argumentList.size()]);

        options = (Option[]) optionList.toArray(
                new Option[optionList.size()] );

        parsed = true;
    }

    private int processOption(char first, String[] args, int i,
                              ArrayList optionList)
            throws CommandLineException
    {
        int skip = 0;   // Default: don't skip any arguments

        // Get the option name by stripping of the prefix.
        String optionName = args[i].substring(1);

        // Find the option by name.
        OptionDefinition o = (OptionDefinition) optionDefinitions.get(optionName);

        if (o == null)
            throw new CommandLineException("Unknown option: '" + args[i] + "'");

        // Process it.
        skip = o.process(first,args,i,optionList);

        return skip;
    }

    /**
     * General information about an argument.
     */
    class ArgumentBase
    {
        /** The absolute position in the original argument array. **/
        private int position;

        public ArgumentBase(int position)
        {
            this.position = position;
        }

        public int getPosition()
        {
            return position;
        }

        public String getValue()
        {
            return args[position];
        }
    }

    /**
     * A non-option command line argument.
     */
    class Argument extends ArgumentBase
    {
        /** The arugment position (without options). **/
        private int argumentPosition;

        public Argument(int position, int argPosition)
        {
            super(position);
            this.argumentPosition = argPosition;
        }

        public int getArgumentPosition()
        {
            return argumentPosition;
        }
    }

    /**
     * Option command line argument.
     */
    class Option extends ArgumentBase
    {

        private OptionDefinition definition;

        public Option(int position,OptionDefinition def)
        {
            super(position);
            definition = def;
        }

        public OptionDefinition getDefinition()
        {
            return definition;
        }
    }

    /**
     * Defines an option for the command line parser.
     */
    class OptionDefinition
    {
        /** The name of the option. **/
        private String name;
        /** A list of the instances of this option definition. **/
        private ArrayList instances;

        OptionDefinition(String name)
        {
            this.name = name;
            instances = new ArrayList();
        }

        int process(char first, String[] args, int i,ArrayList optionList)
            throws CommandLineException
        {
            Option o = new Option(i,this);
            optionList.add(o);
            instances.add(o);
            return 0;
        }

        public String getName()
        {
            return name;
        }

        int getInstanceCount()
        {
            return instances.size();
        }

    }



}

package verification;

import exceptions.function.FunctionAlreadyExistsException;
import exceptions.variable.VariableDuplicateNameException;
import exceptions.typedef.TypeNotDefinedException;
import table.TypeTable;

import java.util.HashSet;
import java.util.List;

public class ContextConditions {

    /**
     * Type tables - Context condition 1:
     * Struct components should be distinct
     *
     * @param structName - name of the struct in the type definition
     * @param names      - list of components' names
     * @return - true, when names are distinct
     * @throws VariableDuplicateNameException - when names aren't distinct
     */
    public static boolean structComponentsNamesDistinct(String structName, List<String> names) throws VariableDuplicateNameException {
        HashSet<String> distinctNames = new HashSet<>();
        for (String name : names) {
            if (!distinctNames.add(name)) {
                throw new VariableDuplicateNameException(structName, name);
            }
        }
        return true;
    }

    /**
     * Type tables - Context condition 2:
     * Struct components' types should be either elementary or previously defined.
     *
     * @param typeNames - list of struct components' type names
     * @return - true, when each type is elementary or previously defined
     * @throws TypeNotDefinedException - when either type is undefined
     */
    public static boolean structComponentsTypesElementaryOrPreviouslyDefined(List<String> typeNames) throws TypeNotDefinedException {
        for (String typeName : typeNames) {
            TypeTable.getInstance().checkTypeDefined(typeName);
        }
        return true;
    }

    /**
     * Type tables - Context condition 3:
     *
     * @param typeName - name of the array type
     * @return - true, when typeName is defined
     * @throws TypeNotDefinedException, when typeName is undefined
     */
    public static boolean arrayTypeElementaryOrPreviouslyDefined(String typeName) throws TypeNotDefinedException {
        TypeTable.getInstance().checkTypeDefined(typeName);
        return true;
    }

    /**
     * Global variables - Context condition 4:
     * global variables' types should be either elementary or previously defined
     *
     * @param typeNames - list of global variable type names
     * @return - true, when all types are defined
     * @throws TypeNotDefinedException, when either type is undefined
     */
    public static boolean globalVariablesTypesElementaryOrPreviouslyDefined(List<String> typeNames) throws TypeNotDefinedException {
        for (String typeName : typeNames) {
            TypeTable.getInstance().checkTypeDefined(typeName);
        }
        return true;
    }

    /**
     * Global variables - Context condition 5:
     * global variables' names should be distinct
     *
     * @param names - list of names of global variables
     * @return true, when names are distinct
     * @throws VariableDuplicateNameException, when names aren't distinct
     */
    public static boolean globalVariablesNamesDistinct(List<String> names) throws VariableDuplicateNameException {
        HashSet<String> distinctNames = new HashSet<>();
        for (String name : names) {
            if (!distinctNames.add(name)) {
                throw new VariableDuplicateNameException("gm", name);
            }
        }
        return true;
    }

    /**
     * Function tables - Context condition 6:
     * Declared function types should be either elementary or previously defined
     * @param typeNames - list of type names
     * @return true, when types are defined
     * @throws TypeNotDefinedException, when types aren't defined
     */
    public static boolean functionTypesElementaryOrPreviouslyDefined(List<String> typeNames) throws TypeNotDefinedException {
        for (String typeName : typeNames) {
            TypeTable.getInstance().checkTypeDefined(typeName);
        }
        return true;
    }

    /**
     * Function tables - Context condition 7:
     * Declared function names should be distinct
     *
     * @param names - list of function names
     * @return true, when names are distinct
     * @throws FunctionAlreadyExistsException, when names aren't distinct
     */
    public static boolean functionNamesDistinct(List<String> names) throws FunctionAlreadyExistsException {
        HashSet<String> distinctNames = new HashSet<>();
        for (String name : names) {
            if (!distinctNames.add(name)) {
                throw new FunctionAlreadyExistsException(name);
            }
        }
        return true;
    }

//    public static

}

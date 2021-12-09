package xyz.arbres.objdiff.core.metamodel.clazz;


import xyz.arbres.objdiff.core.diff.customer.CustomValueComparator;

/**
 * @author bartosz walacik
 */
public class ValueDefinition extends ClientsClassDefinition {
    private CustomValueComparator customValueComparator;

    public ValueDefinition(Class<?> clazz) {
        super(clazz);
    }

    public ValueDefinition(Class<?> clazz, CustomValueComparator customValueComparator) {
        super(clazz);
        this.customValueComparator = customValueComparator;
    }

    @Deprecated
    public void setCustomValueComparator(CustomValueComparator customValueComparator) {
        this.customValueComparator = customValueComparator;
    }

    public CustomValueComparator getComparator() {
        return customValueComparator;
    }
}

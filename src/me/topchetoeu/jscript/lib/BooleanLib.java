package me.topchetoeu.jscript.lib;

import me.topchetoeu.jscript.engine.values.ObjectValue;
import me.topchetoeu.jscript.engine.values.Values;
import me.topchetoeu.jscript.interop.Arguments;
import me.topchetoeu.jscript.interop.Expose;
import me.topchetoeu.jscript.interop.ExposeConstructor;
import me.topchetoeu.jscript.interop.WrapperName;

@WrapperName("Boolean")
public class BooleanLib {
    public static final BooleanLib TRUE = new BooleanLib(true);
    public static final BooleanLib FALSE = new BooleanLib(false);

    public final boolean value;

    @Override public String toString() {
        return value + "";
    }

    public BooleanLib(boolean val) {
        this.value = val;
    }

    @ExposeConstructor public static Object __constructor(Arguments args) {
        var val = args.getBoolean(0);
        if (args.self instanceof ObjectValue) return val ? TRUE : FALSE;
        else return val;
    }
    @Expose public static String __toString(Arguments args) {
        return args.self(Boolean.class) ? "true" : "false";
    }
    @Expose public static boolean __valueOf(Arguments args) {
        if (Values.isWrapper(args.self, BooleanLib.class)) return Values.wrapper(args.self, BooleanLib.class).value;
        return args.self(Boolean.class);
    }
}

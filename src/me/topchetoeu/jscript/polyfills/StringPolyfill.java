package me.topchetoeu.jscript.polyfills;

import java.util.regex.Pattern;

import me.topchetoeu.jscript.engine.Context;
import me.topchetoeu.jscript.engine.Environment;
import me.topchetoeu.jscript.engine.values.ArrayValue;
import me.topchetoeu.jscript.engine.values.FunctionValue;
import me.topchetoeu.jscript.engine.values.ObjectValue;
import me.topchetoeu.jscript.engine.values.Values;
import me.topchetoeu.jscript.exceptions.EngineException;
import me.topchetoeu.jscript.interop.InitType;
import me.topchetoeu.jscript.interop.Native;
import me.topchetoeu.jscript.interop.NativeConstructor;
import me.topchetoeu.jscript.interop.NativeGetter;
import me.topchetoeu.jscript.interop.NativeInit;

// TODO: implement index wrapping properly
public class StringPolyfill {
    public final String value;

    private static String passThis(Context ctx, String funcName, Object val) throws InterruptedException {
        if (val instanceof StringPolyfill) return ((StringPolyfill)val).value;
        else if (val instanceof String) return (String)val;
        else throw EngineException.ofType(String.format("'%s' may only be called upon object and primitve strings.", funcName));
    }
    private static int normalizeI(int i, int len, boolean clamp) {
        if (i < 0) i += len;
        if (clamp) {
            if (i < 0) i = 0;
            if (i >= len) i = len;
        }
        return i;
    }

    @NativeGetter(thisArg = true) public static int length(Context ctx, Object thisArg) throws InterruptedException {
        return passThis(ctx, "substring", thisArg).length();
    }

    @Native(thisArg = true) public static String substring(Context ctx, Object thisArg, int start, Object _end) throws InterruptedException {
        var val = passThis(ctx, "substring", thisArg);
        start = normalizeI(start, val.length(), true);
        int end = normalizeI(_end == null ? val.length() : (int)Values.toNumber(ctx, _end), val.length(), true);

        return val.substring(start, end);
    }
    @Native(thisArg = true) public static String substr(Context ctx, Object thisArg, int start, Object _len) throws InterruptedException {
        var val = passThis(ctx, "substr", thisArg);
        int len = _len == null ? val.length() - start : (int)Values.toNumber(ctx, _len);
        return substring(ctx, val, start, start + len);
    }

    @Native(thisArg = true) public static String toLowerCase(Context ctx, Object thisArg) throws InterruptedException {
        return passThis(ctx, "toLowerCase", thisArg).toLowerCase();
    }
    @Native(thisArg = true) public static String toUpperCase(Context ctx, Object thisArg) throws InterruptedException {
        return passThis(ctx, "toUpperCase", thisArg).toUpperCase();
    }

    @Native(thisArg = true) public static String charAt(Context ctx, Object thisArg, int i) throws InterruptedException {
        return passThis(ctx, "charAt", thisArg).charAt(i) + "";
    }
    @Native(thisArg = true) public static int charCodeAt(Context ctx, Object thisArg, int i) throws InterruptedException {
        return passThis(ctx, "charCodeAt", thisArg).charAt(i);
    }

    @Native(thisArg = true) public static boolean startsWith(Context ctx, Object thisArg, String term, int pos) throws InterruptedException {
        return passThis(ctx, "startsWith", thisArg).startsWith(term, pos);
    }
    @Native(thisArg = true) public static boolean endsWith(Context ctx, Object thisArg, String term, int pos) throws InterruptedException {
        var val = passThis(ctx, "endsWith", thisArg);
        return val.lastIndexOf(term, pos) >= 0;
    }

    @Native(thisArg = true) public static int indexOf(Context ctx, Object thisArg, Object term, int start) throws InterruptedException {
        var val = passThis(ctx, "indexOf", thisArg);

        if (term != null && term != Values.NULL && !(term instanceof String)) {
            var search = Values.getMember(ctx, term, ctx.env.symbol("Symbol.search"));
            if (search instanceof FunctionValue) {
                return (int)Values.toNumber(ctx, ((FunctionValue)search).call(ctx, term, val, false, start));
            }
        }

        return val.indexOf(Values.toString(ctx, term), start);
    }
    @Native(thisArg = true) public static int lastIndexOf(Context ctx, Object thisArg, Object term, int pos) throws InterruptedException {
        var val = passThis(ctx, "lastIndexOf", thisArg);

        if (term != null && term != Values.NULL && !(term instanceof String)) {
            var search = Values.getMember(ctx, term, ctx.env.symbol("Symbol.search"));
            if (search instanceof FunctionValue) {
                return (int)Values.toNumber(ctx, ((FunctionValue)search).call(ctx, term, val, true, pos));
            }
        }

        return val.lastIndexOf(Values.toString(ctx, term), pos);
    }

    @Native(thisArg = true) public static boolean includes(Context ctx, Object thisArg, Object term, int pos) throws InterruptedException {
        return lastIndexOf(ctx, passThis(ctx, "includes", thisArg), term, pos) >= 0;
    }

    @Native(thisArg = true) public static String replace(Context ctx, Object thisArg, Object term, String replacement) throws InterruptedException {
        var val = passThis(ctx, "replace", thisArg);

        if (term != null && term != Values.NULL && !(term instanceof String)) {
            var replace = Values.getMember(ctx, term, ctx.env.symbol("Symbol.replace"));
            if (replace instanceof FunctionValue) {
                return Values.toString(ctx, ((FunctionValue)replace).call(ctx, term, val, replacement));
            }
        }

        return val.replaceFirst(Pattern.quote(Values.toString(ctx, term)), replacement);
    }
    @Native(thisArg = true) public static String replaceAll(Context ctx, Object thisArg, Object term, String replacement) throws InterruptedException {
        var val = passThis(ctx, "replaceAll", thisArg);

        if (term != null && term != Values.NULL && !(term instanceof String)) {
            var replace = Values.getMember(ctx, term, ctx.env.symbol("Symbol.replace"));
            if (replace instanceof FunctionValue) {
                return Values.toString(ctx, ((FunctionValue)replace).call(ctx, term, val, replacement));
            }
        }

        return val.replaceFirst(Pattern.quote(Values.toString(ctx, term)), replacement);
    }

    @Native(thisArg = true) public static ArrayValue match(Context ctx, Object thisArg, Object term, String replacement) throws InterruptedException {
        var val = passThis(ctx, "match", thisArg);

        FunctionValue match;
        
        try {
            var _match = Values.getMember(ctx, term, ctx.env.symbol("Symbol.match"));
            if (_match instanceof FunctionValue) match = (FunctionValue)_match;
            else if (ctx.env.regexConstructor != null) {
                var regex = Values.callNew(ctx, ctx.env.regexConstructor, Values.toString(ctx, term), "");
                _match = Values.getMember(ctx, regex, ctx.env.symbol("Symbol.match"));
                if (_match instanceof FunctionValue) match = (FunctionValue)_match;
                else throw EngineException.ofError("Regular expressions don't support matching.");
            }
            else throw EngineException.ofError("Regular expressions not supported.");
        }
        catch (IllegalArgumentException e) { return new ArrayValue(ctx, ""); }

        var res = match.call(ctx, term, val);
        if (res instanceof ArrayValue) return (ArrayValue)res;
        else return new ArrayValue(ctx, "");
    }
    @Native(thisArg = true) public static Object matchAll(Context ctx, Object thisArg, Object term, String replacement) throws InterruptedException {
        var val = passThis(ctx, "matchAll", thisArg);

        FunctionValue match = null;
        
        try {
            var _match = Values.getMember(ctx, term, ctx.env.symbol("Symbol.matchAll"));
            if (_match instanceof FunctionValue) match = (FunctionValue)_match;
        }
        catch (IllegalArgumentException e) { }

        if (match == null && ctx.env.regexConstructor != null) {
            var regex = Values.callNew(ctx, ctx.env.regexConstructor, Values.toString(ctx, term), "g");
            var _match = Values.getMember(ctx, regex, ctx.env.symbol("Symbol.matchAll"));
            if (_match instanceof FunctionValue) match = (FunctionValue)_match;
            else throw EngineException.ofError("Regular expressions don't support matching.");
        }
        else throw EngineException.ofError("Regular expressions not supported.");

        return match.call(ctx, term, val);
    }

    @Native(thisArg = true) public static ArrayValue split(Context ctx, Object thisArg, Object term, Object lim, boolean sensible) throws InterruptedException {
        var val = passThis(ctx, "split", thisArg);

        if (lim != null) lim = Values.toNumber(ctx, lim);

        if (term != null && term != Values.NULL && !(term instanceof String)) {
            var replace = Values.getMember(ctx, term, ctx.env.symbol("Symbol.replace"));
            if (replace instanceof FunctionValue) {
                var tmp = ((FunctionValue)replace).call(ctx, term, val, lim, sensible);

                if (tmp instanceof ArrayValue) {
                    var parts = new ArrayValue(((ArrayValue)tmp).size());
                    for (int i = 0; i < parts.size(); i++) parts.set(ctx, i, Values.toString(ctx, ((ArrayValue)tmp).get(i)));
                    return parts;
                }
            }
        }

        String[] parts;
        var pattern = Pattern.quote(Values.toString(ctx, term));

        if (lim == null) parts = val.split(pattern);
        else if (sensible) parts = val.split(pattern, (int)(double)lim);
        else {
            var limit = (int)(double)lim;
            parts = val.split(pattern, limit + 1);
            ArrayValue res;

            if (parts.length > limit) res = new ArrayValue(limit);
            else res = new ArrayValue(parts.length);

            for (var i = 0; i < parts.length && i < limit; i++) res.set(ctx, i, parts[i]);

            return res;
        }

        var res = new ArrayValue(parts.length);
        var i = 0;

        for (; i < parts.length; i++) {
            if (lim != null && (double)lim <= i) break;
            res.set(ctx, i, parts[i]);
        }

        return res;
    }

    @Native(thisArg = true) public static String slice(Context ctx, Object thisArg, int start, Object _end) throws InterruptedException {
        return substring(ctx, passThis(ctx, "slice", thisArg), start, _end);
    }

    @Native(thisArg = true) public static String concat(Context ctx, Object thisArg, Object... args) throws InterruptedException {
        var res = new StringBuilder(passThis(ctx, "concat", thisArg));

        for (var el : args) res.append(Values.toString(ctx, el));

        return res.toString();
    }
    @Native(thisArg = true) public static String trim(Context ctx, Object thisArg) throws InterruptedException {
        return passThis(ctx, "trim", thisArg).trim();
    }

    @NativeConstructor(thisArg = true) public static Object constructor(Context ctx, Object thisArg, Object val) throws InterruptedException {
        val = Values.toString(ctx, val);
        if (thisArg instanceof ObjectValue) return new StringPolyfill((String)val);
        else return val;
    }
    @Native(thisArg = true) public static String toString(Context ctx, Object thisArg) throws InterruptedException {
        return passThis(ctx, "toString", thisArg);
    }
    @Native(thisArg = true) public static String valueOf(Context ctx, Object thisArg) throws InterruptedException {
        return passThis(ctx, "valueOf", thisArg);
    }

    @Native public static String fromCharCode(int ...val) {
        char[] arr = new char[val.length];
        for (var i = 0; i < val.length; i++) arr[i] = (char)val[i];
        return new String(arr);
    }

    public StringPolyfill(String val) {
        this.value = val;
    }

    @NativeInit(InitType.PROTOTYPE) public static void init(Environment env, ObjectValue target) {
        target.defineProperty(null, env.symbol("Symbol.typeName"), "String");
    }
}
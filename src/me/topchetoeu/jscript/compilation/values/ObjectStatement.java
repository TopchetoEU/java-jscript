package me.topchetoeu.jscript.compilation.values;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.topchetoeu.jscript.Location;
import me.topchetoeu.jscript.compilation.Instruction;
import me.topchetoeu.jscript.compilation.Statement;
import me.topchetoeu.jscript.engine.scope.ScopeRecord;

public class ObjectStatement extends Statement {
    public final Map<Object, Statement> map;
    public final Map<Object, FunctionStatement> getters;
    public final Map<Object, FunctionStatement> setters;

    @Override
    public boolean pollutesStack() { return true; }

    @Override
    public void compile(List<Instruction> target, ScopeRecord scope) {
        target.add(Instruction.loadObj().locate(loc()));
        if (!map.isEmpty()) target.add(Instruction.dup(map.size()).locate(loc()));

        for (var el : map.entrySet()) {
            target.add(Instruction.loadValue(el.getKey()).locate(loc()));
            var val = el.getValue();
            if (val instanceof FunctionStatement) ((FunctionStatement)val).compile(target, scope, el.getKey().toString(), false);
            else val.compileWithPollution(target, scope);
            target.add(Instruction.storeMember().locate(loc()));
        }

        var keys = new ArrayList<Object>();
        keys.addAll(getters.keySet());
        keys.addAll(setters.keySet());

        for (var key : keys) {
            if (key instanceof String) target.add(Instruction.loadValue((String)key).locate(loc()));
            else target.add(Instruction.loadValue((Double)key).locate(loc()));

            if (getters.containsKey(key)) getters.get(key).compileWithPollution(target, scope);
            else target.add(Instruction.loadValue(null).locate(loc()));

            if (setters.containsKey(key)) setters.get(key).compileWithPollution(target, scope);
            else target.add(Instruction.loadValue(null).locate(loc()));

            target.add(Instruction.defProp().locate(loc()));
        }
    }

    public ObjectStatement(Location loc, Map<Object, Statement> map, Map<Object, FunctionStatement> getters, Map<Object, FunctionStatement> setters) {
        super(loc);
        this.map = map;
        this.getters = getters;
        this.setters = setters;
    }
}

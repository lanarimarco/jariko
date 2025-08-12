# Implementation Summary: RPG IN and OUT Opcodes

This document summarizes the implementation of RPG fixed format opcodes IN and OUT for the Jariko interpreter.

## Files Modified

### 1. Configuration.kt
- Added `readDataArea` callback to JarikoCallback
- Added `writeDataArea` callback to JarikoCallback
- Both callbacks include default implementations for backward compatibility

### 2. statements.kt
- Added `InStmt` data class for IN operations
- Added `OutStmt` data class for OUT operations
- Both implement `execute()` methods that call the appropriate JarikoCallback methods
- Handle Factor1 (*LOCK), Factor2 (data area name), Result field, and indicators

### 3. serialization.kt
- Added `InStmt` and `OutStmt` to the statement subclasses list
- Ensures proper serialization support

### 4. misc.kt (parsetreetoast)
- Added dispatcher cases for `csIN()` and `csOUT()` in `Cspec_fixed_standardContext.toAst()`
- Implemented `CsINContext.toAst()` method
- Implemented `CsOUTContext.toAst()` method
- Both methods properly parse Factor1, Factor2, Result, and indicators

## Implementation Features

### IN Statement Support
- Factor1: *LOCK support for data area locking
- Factor2: Data area name (optional when using DEFINE)
- Result: Target field where data is placed
- Indicators: Error handling indicators
- Calls `JarikoCallback.readDataArea(dataAreaName, lock)`

### OUT Statement Support  
- Factor1: *LOCK support for data area locking
- Factor2: Data area name (optional when using DEFINE)
- Result: Source field containing data to write
- Indicators: Error handling indicators
- Calls `JarikoCallback.writeDataArea(dataAreaName, value, lock)`

### DEFINE Integration
- When Factor2 is omitted, attempts to resolve data area name from DEFINE statements
- Simplified implementation that uses target/source variable name

## Test Files Created

### Test RPG Programs
- `DTAREAREAD.rpgle` - Basic IN operation test
- `DTAREAWRITE.rpgle` - Basic OUT operation test
- `DTAREAREADIND.rpgle` - IN operation with indicators
- `DTAREAPROC.rpgle` - Data area operations in procedures (DEFINE outside)
- `DTAREAINPROC.rpgle` - Data area operations in procedures (DEFINE inside)

### Unit Tests
- `DataAreaTest.kt` - Tests for callback invocation and basic functionality

## Callback Interface

```kotlin
var readDataArea: ((dataAreaName: String, lock: Boolean) -> String) = { dataAreaName, _ ->
    dataAreaName // Default implementation
}

var writeDataArea: ((dataAreaName: String, value: String, lock: Boolean) -> Unit) = { _, _, _ ->
    // Default implementation does nothing
}
```

## Usage Example

```kotlin
val configuration = Configuration().apply {
    jarikoCallback = JarikoCallback().apply {
        readDataArea = { dataAreaName, lock ->
            // Custom implementation
            myDataAreaManager.read(dataAreaName, lock)
        }
        writeDataArea = { dataAreaName, value, lock ->
            // Custom implementation  
            myDataAreaManager.write(dataAreaName, value, lock)
        }
    }
}
```

## Limitations

1. **DEFINE Resolution**: The current implementation uses a simplified approach for resolving data area names when Factor2 is omitted. A full implementation would require:
   - Tracking DEFINE statements during compilation
   - Resolving the original data area name from the target variable
   - Maintaining a symbol table mapping

2. **Error Handling**: Basic error handling is implemented but could be enhanced with more specific error types and better error recovery.

3. **Data Area Types**: The implementation assumes string-based data areas. Support for typed data areas could be added in the future.

## Testing Status

- ✅ AST parsing implemented
- ✅ Statement execution logic implemented  
- ✅ Callback integration implemented
- ✅ Test files created
- ⏳ Full integration testing (requires build environment)
- ⏳ Performance testing
- ⏳ Error scenario testing

## Compatibility

The implementation is fully backward compatible:
- Default callback implementations prevent breaking changes
- Existing code will continue to work without modification
- New functionality is opt-in via callback configuration

## Future Enhancements

1. **Enhanced DEFINE Resolution**: Implement full symbol table tracking for data area name resolution
2. **Typed Data Areas**: Support for numeric and structured data areas
3. **Error Recovery**: Enhanced error handling and recovery mechanisms
4. **Performance Optimization**: Optimize callback invocation and data transfer
5. **Additional Operations**: Support for UNLOCK and other data area operations
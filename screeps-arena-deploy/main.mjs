import {instantiate} from "./arena.js";
import WASM_BIN from "./wasm.bin"

let entryPoint;
instantiate(WASM_BIN)
    .then(source => {
        entryPoint = source.instance.exports.Konan_js_main;
    });

export function loop() {
    console.log(entryPoint(1, 0));
}
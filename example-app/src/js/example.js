import { ZPrinter } from 'zprinter';

window.testEcho = () => {
    const inputValue = document.getElementById("echoInput").value;
    ZPrinter.echo({ value: inputValue })
}

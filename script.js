"use strict";

document.querySelector(".btn").addEventListener("click", function () {
  const lOperand = document.querySelector(".left-operand"),
    rOperand = document.querySelector(".right-operand");
  const operator = document.querySelectorAll(".operator");
  let selectedOperator;

  console.log(operator);

  if (!lOperand.valueAsNumber || !rOperand.valueAsNumber) return;

  console.log(operator[0]);
  for (let i = 0; i < operator.length; i++)
    if (operator[i].checked) selectedOperator = operator[i].value;

  console.log(
    `${lOperand.valueAsNumber} ${selectedOperator} ${rOperand.valueAsNumber}`
  );
  console.log(lOperand.valueAsNumber, selectedOperator, rOperand.valueAsNumber);
});

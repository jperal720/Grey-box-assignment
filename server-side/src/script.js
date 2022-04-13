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

  const message = ``; //Contains the operation
  getJsonFile(message);
});

const getJsonFile = function (message) {
  fetch(`http://localhost:8080/calculate`)
    .then((response) => response.json())
    .then((data) => {
      console.log(data);
    });
};

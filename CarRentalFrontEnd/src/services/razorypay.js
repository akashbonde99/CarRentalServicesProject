export async function initiatePayment(amount) {
  try {
    const response = await fetch(
      "http://localhost:8080/api/payments/create-order",
      {
        method: "POST",
        headers: {
          "Content-Type": "application/x-www-form-urlencoded",
        },
        body: new URLSearchParams({
          amount: amount,
          currency: "INR",
        }),
      }
    );

    const order = await response.json();

    const options = {
      key: "rzp_test_S9HcVYCQnsXY5t",
      amount: order.amount,
      currency: order.currency,
      name: "Car Booking Website",
      description: "Car Booking Payment",
      order_id: order.id,
      handler: function (response) {
        alert("Payment Successful");
        console.log(response);
      },
      theme: {
        color: "#2563eb",
      },
    };

    const razorpay = new window.Razorpay(options);
    razorpay.open();

  } catch (error) {
    console.error(error);
    alert("Payment Failed");
  }
}

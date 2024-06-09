CREATE TABLE "order" (
  id TEXT PRIMARY KEY,
  user_id TEXT NOT NULL,
  placing_timestamp timestamptz NOT NULL
);

CREATE TABLE order_line (
  order_id TEXT NOT NULL,
  product_id TEXT NOT NULL,
  quantity INT NOT NULL,

 PRIMARY KEY(order_id, product_id)
);

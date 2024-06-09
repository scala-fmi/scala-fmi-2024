package fmi.inventory

import cats.effect.IO
import cats.syntax.all.*
import doobie.implicits.*
import fmi.infrastructure.db.DoobieDatabase.DbTransactor

class ProductDao(dbTransactor: DbTransactor):
  def retrieveProduct(id: ProductId): IO[Option[Product]] =
    sql"""
      SELECT id, name, description, weight_in_grams
      FROM product
      WHERE id = $id
    """
      .query[Product]
      .option
      .transact(dbTransactor)

  def addProduct(product: Product): IO[Unit] =
    sql"""
      INSERT INTO product as p (id, name, description, weight_in_grams)
      VALUES (${product.id}, ${product.name}, ${product.description}, ${product.weightInGrams})
      ON CONFLICT (id) DO UPDATE SET
      name = EXCLUDED.name,
      description = EXCLUDED.description,
      weight_in_grams = EXCLUDED.weight_in_grams
    """.update.run.void
      .transact(dbTransactor)

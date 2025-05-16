#1. Find customers who rented a specific film:
#Retrieve the names of customers who rented the film with the title 'ACADEMY DINOSAUR'.
select c.first_name,c.last_name from customer c  join 
rental r on c.customer_id=r.customer_id join
 inventory i on r.inventory_id=i.inventory_id join
 film f on i.film_id=f.film_id where f.title=
 (select title from film where title='ACADEMY DINOSAUR');
 
 
#2. Find films rented by a specific customer:
#Retrieve the titles of films rented by a customer with a given customer_id.
select f.title from film f join 
inventory i on f.film_id=i.film_id join
 rental r on i.inventory_id=r.inventory_id join 
customer c on r.customer_id=c.customer_id where c.customer_id=
(select customer_id from customer where customer_id='100');


#3. Find rental details for the most recent rental:
#Retrieve the rental details for the most recent rental transaction in the database.
select c.customer_id,c.first_name,c.last_name,r.rental_date,count(r.rental_date) from customer c join rental r on c.customer_id=r.customer_id where r.rental_date=
(select max(rental_date) from rental) group by c.customer_id,c.first_name,c.last_name,r.rental_date order by rental_date;


#4. Find the average rental duration compared to the overall average:
#Compare the average rental duration of films in a specific category with the overall average rental duration.
select cat.category_id,cat.name,AVG(f.rental_duration) as average, IF(avg(f.rental_duration)>
(select avg(rental_duration) from film), "MORE", "LESS") as comparison_with_other from category cat join
film_category fc on cat.category_id=fc.category_id join
 film f on fc.film_id=f.film_id group by cat.category_id,cat.name;
 
 
#5. Find films with rental counts above average:
#Retrieve films with rental counts greater than the average number of rentals across all films.
#- Count film wise rental
#- Display films whose rental count is > avg rental count

select f.film_id,f.title,count(r.rental_id) as rental_count from film f join
inventory i on f.film_id=i.film_id join
rental r on i.inventory_id=r.inventory_id group by f.film_id,f.title having count(r.rental_id)>all
(select avg(a.rcount) from (select count(r.rental_id) as rcount from rental r join inventory i on r.inventory_id=i.inventory_id join film f on i.film_id=f.film_id group by f.film_id)as a);

#6. Find customers who rented the same film as another customer:
#Identify customers who rented the same film as a specific customer (based on customer_id).
select c.customer_id, c.first_name,c.last_name,f.title from customer c join rental r on c.customer_id=r.customer_id join
inventory i on r.inventory_id=i.inventory_id join
film f on i.film_id=f.film_id where f.title=any
(select f.title from film f join
inventory i on f.film_id=i.film_id join
rental r on i.inventory_id=r.inventory_id join
customer c on r.customer_id=c.customer_id where c.customer_id=100);


#7. Find customers who have rented from both stores:
#Identify customers who have rented films from both store_id 1 and store_id 2.
select c.customer_id,c.first_name,c.last_name,s.store_id from customer c join
rental r on c.customer_id=r.customer_id join
inventory i on r.inventory_id=i.inventory_id join
store s on i.store_id=s.store_id where s.store_id=all
(select store_id from store) ;


#8. Find actors who have appeared in the same film:
#Retrieve actors who have appeared in the same film as a specific actor (based on actor_id).
select a.actor_id, a.first_name,a.last_name,f.title as actor_in_film from actor a  join
film_actor fa on a.actor_id=fa.actor_id join
film f on fa.film_id=f.film_id where f.title= any
(select f.title from film f join
film_actor fa on f.film_id=fa.film_id  join
actor a on fa.actor_id=a.actor_id where a.actor_id=4);


#9. Find films rented by top-revenue customers:
#Retrieve film titles rented by customers who are among the top 10 in terms of total rental revenue.
select c.customer_id,f.title,sum(p.amount) as total from film f join
inventory i on f.film_id=i.film_id join
rental r on i.inventory_id=r.inventory_id join
customer c on r.customer_id=c.customer_id join
payment p on c.customer_id=p.customer_id group by c.customer_id,f.title having sum(p.amount) in
(select sum(p.amount) from payment p join
customer c on p.customer_id=c.customer_id group by c.customer_id) order by total desc limit 10;


#10. Find films that have not been rented:
#Retrieve titles of films that have not been rented by any customer.
SELECT F.TITLE FROM FILM F WHERE  F.FILM_ID in (SELECT I.FILM_ID FROM INVENTORY I WHERE I.INVENTORY_ID NOT IN (SELECT R.INVENTORY_ID FROM RENTAL R))
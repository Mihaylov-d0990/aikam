PGDMP         9                z            aikam    14.5    14.5                0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                      false                       0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                      false                       0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                      false                       1262    16394    aikam    DATABASE     P   CREATE DATABASE aikam WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE = 'C';
    DROP DATABASE aikam;
                postgres    false            ?            1259    16401    customer    TABLE     ?   CREATE TABLE public.customer (
    customerid integer NOT NULL,
    firstname character varying(128) NOT NULL,
    lastname character varying(128) NOT NULL
);
    DROP TABLE public.customer;
       public         heap    postgres    false            ?            1259    16400    customer_customerid_seq    SEQUENCE     ?   CREATE SEQUENCE public.customer_customerid_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 .   DROP SEQUENCE public.customer_customerid_seq;
       public          postgres    false    210                       0    0    customer_customerid_seq    SEQUENCE OWNED BY     S   ALTER SEQUENCE public.customer_customerid_seq OWNED BY public.customer.customerid;
          public          postgres    false    209            ?            1259    16408    product    TABLE     ?   CREATE TABLE public.product (
    productid integer NOT NULL,
    name character varying(128) NOT NULL,
    price integer NOT NULL
);
    DROP TABLE public.product;
       public         heap    postgres    false            ?            1259    16407    product_productid_seq    SEQUENCE     ?   CREATE SEQUENCE public.product_productid_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 ,   DROP SEQUENCE public.product_productid_seq;
       public          postgres    false    212            	           0    0    product_productid_seq    SEQUENCE OWNED BY     O   ALTER SEQUENCE public.product_productid_seq OWNED BY public.product.productid;
          public          postgres    false    211            ?            1259    16415    purchase    TABLE     ?   CREATE TABLE public.purchase (
    purchaseid integer NOT NULL,
    customer integer NOT NULL,
    product integer NOT NULL,
    date date NOT NULL
);
    DROP TABLE public.purchase;
       public         heap    postgres    false            ?            1259    16414    purchase_purchaseid_seq    SEQUENCE     ?   CREATE SEQUENCE public.purchase_purchaseid_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 .   DROP SEQUENCE public.purchase_purchaseid_seq;
       public          postgres    false    214            
           0    0    purchase_purchaseid_seq    SEQUENCE OWNED BY     S   ALTER SEQUENCE public.purchase_purchaseid_seq OWNED BY public.purchase.purchaseid;
          public          postgres    false    213            f           2604    16404    customer customerid    DEFAULT     z   ALTER TABLE ONLY public.customer ALTER COLUMN customerid SET DEFAULT nextval('public.customer_customerid_seq'::regclass);
 B   ALTER TABLE public.customer ALTER COLUMN customerid DROP DEFAULT;
       public          postgres    false    209    210    210            g           2604    16411    product productid    DEFAULT     v   ALTER TABLE ONLY public.product ALTER COLUMN productid SET DEFAULT nextval('public.product_productid_seq'::regclass);
 @   ALTER TABLE public.product ALTER COLUMN productid DROP DEFAULT;
       public          postgres    false    211    212    212            h           2604    16418    purchase purchaseid    DEFAULT     z   ALTER TABLE ONLY public.purchase ALTER COLUMN purchaseid SET DEFAULT nextval('public.purchase_purchaseid_seq'::regclass);
 B   ALTER TABLE public.purchase ALTER COLUMN purchaseid DROP DEFAULT;
       public          postgres    false    213    214    214            ?          0    16401    customer 
   TABLE DATA           C   COPY public.customer (customerid, firstname, lastname) FROM stdin;
    public          postgres    false    210   S       ?          0    16408    product 
   TABLE DATA           9   COPY public.product (productid, name, price) FROM stdin;
    public          postgres    false    212   ?                 0    16415    purchase 
   TABLE DATA           G   COPY public.purchase (purchaseid, customer, product, date) FROM stdin;
    public          postgres    false    214                     0    0    customer_customerid_seq    SEQUENCE SET     E   SELECT pg_catalog.setval('public.customer_customerid_seq', 3, true);
          public          postgres    false    209                       0    0    product_productid_seq    SEQUENCE SET     C   SELECT pg_catalog.setval('public.product_productid_seq', 5, true);
          public          postgres    false    211                       0    0    purchase_purchaseid_seq    SEQUENCE SET     F   SELECT pg_catalog.setval('public.purchase_purchaseid_seq', 29, true);
          public          postgres    false    213            j           2606    16406    customer customer_pkey 
   CONSTRAINT     \   ALTER TABLE ONLY public.customer
    ADD CONSTRAINT customer_pkey PRIMARY KEY (customerid);
 @   ALTER TABLE ONLY public.customer DROP CONSTRAINT customer_pkey;
       public            postgres    false    210            l           2606    16413    product product_pkey 
   CONSTRAINT     Y   ALTER TABLE ONLY public.product
    ADD CONSTRAINT product_pkey PRIMARY KEY (productid);
 >   ALTER TABLE ONLY public.product DROP CONSTRAINT product_pkey;
       public            postgres    false    212            n           2606    16420    purchase purchase_pkey 
   CONSTRAINT     \   ALTER TABLE ONLY public.purchase
    ADD CONSTRAINT purchase_pkey PRIMARY KEY (purchaseid);
 @   ALTER TABLE ONLY public.purchase DROP CONSTRAINT purchase_pkey;
       public            postgres    false    214            o           2606    16421    purchase purchase_customer_fkey    FK CONSTRAINT     ?   ALTER TABLE ONLY public.purchase
    ADD CONSTRAINT purchase_customer_fkey FOREIGN KEY (customer) REFERENCES public.customer(customerid);
 I   ALTER TABLE ONLY public.purchase DROP CONSTRAINT purchase_customer_fkey;
       public          postgres    false    214    210    3178            p           2606    16426    purchase purchase_product_fkey    FK CONSTRAINT     ?   ALTER TABLE ONLY public.purchase
    ADD CONSTRAINT purchase_product_fkey FOREIGN KEY (product) REFERENCES public.product(productid);
 H   ALTER TABLE ONLY public.purchase DROP CONSTRAINT purchase_product_fkey;
       public          postgres    false    214    3180    212            ?   P   x?3??0??ދM?]??yaƅM6\??l?2??0?????@????Ɯ&9?/l???=??֋M??1z\\\ 	:,      ?   Z   x??!?0DQ={?m)??0%H?`?VHz??? ?߁'_^,*?Y[W>?T??R?mb?W?<l?U"?Zb??w{????|? )j         ?   x?]???0?s?K
??Gޥ???R??N|0@A??5j???RBa݁O<_?7?7d??????钠\6&j`w!8\N? W?n??R??eC?d???!?
??????	&u0M?	-??I?]ۅ?}??R     
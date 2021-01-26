-- **************************************************************************
--   This module is part of the SONARIS/Application Framework.       
-- 
--   (c) 2001-2007  ORIMOS S.A.
--                  Innere Gueterstrasse 4
--                  6304 Zug (Switzerland)
-- 
--   All rights reserved. The use of the software without express written   
--   permission of ORIMOS S.A. is strictly prohibited.  
-- 
-- **************************************************************************

-- THIS SCRIPT DEFINES THE TABLE SCHEMA FOR SONARIS/FRAMEWORK APPLICATION
-- DATABASES. IT CONTAINS SYNTAX EXTENSIONS WHICH ARE SPECIFIC TO
-- SONARIS/FRAMEWORK.
--
-- DO NOT ATTEMPT TO USE THIS SCRIPT IN CONJUNCTION WITH DATABASE TOOLS OTHER
-- THAN THOSE SUPPLIED WITH SONARIS/FRAMEWORK e.g. safsql.
-- ALL SCRIPTS WRITTEN IN SPECIFIC SQL DIALECTS FOR USE WITH NATIVE DATABASE
-- UTILITIES SUCH AS DATABASE SCHEMA SCRIPTS ARE SUPPLIED WITH FILENAME
-- SUFFIXES APPROPRIATE TO THE DIALECT IN QUESTION.


--	Set aliases for datatypes.
var BIGINT = ALIAS bigint;
var DOUBLE = ALIAS double;
var DATETIME = ALIAS datetime;

--	SAOInstances table - lists details of each SAO instance, uniquely
--	identified by ContainerID and SAO_ID. Describes the SAOs functional
--	type, hierarchical position and name, and other behavioural attributes.
CREATE TABLE SAOInstances (
	ContainerID int NOT NULL,
	SAO_ID      %BIGINT% NOT NULL,

	LibID       smallint NOT NULL,
	TypeID      smallint NOT NULL,
	ParentID    %BIGINT% NOT NULL,
	NextChildID %BIGINT% NOT NULL,

	Name        varchar (64) NOT NULL,

	History     smallint NOT NULL,
	LogLevel    smallint NOT NULL,

	OwnerID     int NOT NULL,
	GroupID     int NOT NULL,
	Permissions smallint NOT NULL,

	LastChange  %DATETIME% NOT NULL,
	ChangeFlag  int NOT NULL,

	PRIMARY KEY (ContainerID, SAO_ID)
);


--	Connections table - lists dependency relationships between SAOs
CREATE TABLE Connections (
	SrcContainerID int NOT NULL,
	SrcSAO_ID      %BIGINT% NOT NULL,
	DstContainerID int NOT NULL,
	DstSAO_ID      %BIGINT% NOT NULL,
	DstInputNr     int NOT NULL,
	Properties     smallint NULL,

	PRIMARY KEY (DstContainerID, DstSAO_ID, DstInputNr)
);

--	SAOValuesInt table - lists last recorded values for SAOs whose storage
--	type is SAF_INT (single row with Idx 0) or SAF_INT_ARRAY (multiple rows
--	with Idx 1 to n). Gives LastChange time and ChangeFlag with details of
--	how the last change was made.
CREATE TABLE SAOValuesInt (
	ContainerID    int NOT NULL,
	SAO_ID         %BIGINT% NOT NULL,

	Idx            smallint NOT NULL,
	Val            int NOT NULL,

	LastChange     %DATETIME% NOT NULL,
	ChangeFlag     int NOT NULL,

	PRIMARY KEY (ContainerID, SAO_ID, Idx)
);

--	SAOValuesInt64 table - same as SAOValuesInt, but uses two int fields to
--	represent 64 bit values as not all databases use 64 bit ints.
CREATE TABLE SAOValuesInt64 (
	ContainerID    int NOT NULL,
	SAO_ID         %BIGINT% NOT NULL,

	Idx            smallint NOT NULL,
	HVal           int NOT NULL,
	LVal           int NOT NULL,

	LastChange     %DATETIME% NOT NULL,
	ChangeFlag     int NOT NULL,

	PRIMARY KEY (ContainerID, SAO_ID, Idx)
);

--	SAOValuesDouble table - same as SAOValuesInt, but with float val field.
--	N.B. Not all databases will support a double - we may need to check
--	the values we write to be sure this doesn't overflow.
CREATE TABLE SAOValuesDouble (
	ContainerID    int NOT NULL,
	SAO_ID         %BIGINT% NOT NULL,

	Idx            smallint NOT NULL,
	Val            %DOUBLE% NOT NULL,

	LastChange     %DATETIME% NOT NULL,
	ChangeFlag     int NOT NULL,

	PRIMARY KEY (ContainerID, SAO_ID, Idx)
);

--	SAOValuesDate table - same as SAOValuesInt, but with date val field and
--	an int field for milliseconds.
CREATE TABLE SAOValuesDate (
	ContainerID    int NOT NULL,
	SAO_ID         %BIGINT% NOT NULL,

	Idx            smallint NOT NULL,
	Val            %DATETIME% NOT NULL,
	Msecs          int NOT NULL,

	LastChange     %DATETIME% NOT NULL,
	ChangeFlag     int NOT NULL,

	PRIMARY KEY (ContainerID, SAO_ID, Idx)
);

--	SAOValuesString table - as SAOValuesInt, but with text val field.
--	N.B. Strings longer than 255 characters are written as separate rows
--	numbered using the Fragment field.
--	N.B. This table allows NULL in the Val column as Oracle can't store
--	empty strings because it treats them as NULL.
CREATE TABLE SAOValuesString (
	ContainerID    int NOT NULL,
	SAO_ID         %BIGINT% NOT NULL,

	Idx            smallint NOT NULL,
	Fragment       smallint NOT NULL,
	Val            varchar(255),

	LastChange     %DATETIME% NOT NULL,
	ChangeFlag     int NOT NULL,

	PRIMARY KEY (ContainerID, SAO_ID, Idx, Fragment)
);

--	SAOValuesBinary table - same as SAOValuesString
CREATE TABLE SAOValuesBinary (
	ContainerID    int NOT NULL,
	SAO_ID         %BIGINT% NOT NULL,

	Idx            smallint NOT NULL,
	Fragment       smallint NOT NULL,
	Val            varchar(255) NOT NULL,

	LastChange     %DATETIME% NOT NULL,
	ChangeFlag     int NOT NULL,

	PRIMARY KEY (ContainerID, SAO_ID, Idx, Fragment)
);

--	SAFAdmin table
CREATE TABLE SAFAdmin (
	Name           varchar(32) NOT NULL,

	Val            varchar(255) NULL,
	LastChange     %DATETIME% NOT NULL,
	CommentString  varchar(255) NULL,

	PRIMARY KEY (Name)
);

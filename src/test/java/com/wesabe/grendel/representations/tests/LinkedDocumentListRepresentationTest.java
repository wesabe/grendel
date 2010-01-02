package com.wesabe.grendel.representations.tests;

import static org.fest.assertions.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.wesabe.grendel.entities.Document;
import com.wesabe.grendel.entities.User;
import com.wesabe.grendel.representations.LinkedDocumentListRepresentation;

@RunWith(Enclosed.class)
public class LinkedDocumentListRepresentationTest {
	public static class Serializing_A_Document_List {
		private UriInfo uriInfo;
		private LinkedDocumentListRepresentation rep;
		private Document doc;
		private User user, owner;
		
		@Before
		public void setup() throws Exception {
			this.uriInfo = mock(UriInfo.class);
			when(uriInfo.getBaseUriBuilder()).thenAnswer(new Answer<UriBuilder>() {
				@Override
				public UriBuilder answer(InvocationOnMock invocation) throws Throwable {
					return UriBuilder.fromUri("http://example.com");
				}
			});
			
			this.owner = mock(User.class);
			when(owner.getId()).thenReturn("mrpeepers");
			when(owner.toString()).thenReturn("mrpeepers");
			
			this.doc = mock(Document.class);
			when(doc.getName()).thenReturn("document1.txt");
			when(doc.toString()).thenReturn("document1.txt");
			when(doc.getOwner()).thenReturn(owner);
			
			this.user = mock(User.class);
			when(user.getId()).thenReturn("capnfrank");
			when(user.toString()).thenReturn("capnfrank");
			when(user.getLinkedDocuments()).thenReturn(ImmutableSet.of(doc));
			
			this.rep = new LinkedDocumentListRepresentation(uriInfo, user);
		}
		
		@Test
		public void itSerializesIntoJSON() throws Exception {
			final ObjectMapper mapper = new ObjectMapper();
			final String json = mapper.writeValueAsString(rep);
			
			final ObjectNode entity = mapper.readValue(json, ObjectNode.class);
			final List<JsonNode> documents = ImmutableList.copyOf(entity.get("linked-documents").getElements());
			
			assertThat(documents).hasSize(1);
			
			final JsonNode document = documents.get(0);
			assertThat(document.get("name").getTextValue()).isEqualTo("document1.txt");
			assertThat(document.get("uri").getTextValue()).isEqualTo("http://example.com/users/capnfrank/linked-documents/mrpeepers/document1.txt");
			
			final JsonNode owner = document.get("owner");
			assertThat(owner.get("id").getTextValue()).isEqualTo("mrpeepers");
			assertThat(owner.get("uri").getTextValue()).isEqualTo("http://example.com/users/mrpeepers");
		}
	}
}

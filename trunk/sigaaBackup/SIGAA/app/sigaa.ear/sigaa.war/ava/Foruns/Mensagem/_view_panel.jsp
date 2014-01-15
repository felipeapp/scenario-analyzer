<rich:modalPanel id="showModal" minHeight="400" minWidth="700" height="100" width="700">  
        <f:facet name="header">  
            <h:panelGroup>
                <h:outputText value="Mensagem"></h:outputText>
            </h:panelGroup>
        </f:facet>
        <f:facet name="controls">
            <h:panelGroup>
	            <h:outputLink value="#" id="btn1">  
	   		         <h:graphicImage value="/img/close.png"  style="margin-left:5px; cursor:pointer; border: none" />  
	                 <rich:componentControl for="showModal" attachTo="btn1" operation="hide" event="onclick" />  
	            </h:outputLink>
            </h:panelGroup>
        </f:facet>
        
        <fieldset>
			<ul class="show">
			
				<li>
					<br/>
					<table>
						<tr style="width: 10%">
							<%-- Foto e mensagem --%>
							<td align="center" style="background-color: #EFF3FA;" valign="top" rowspan="2" width="10%">				 
								<c:if test="${ not empty _mensagem.usuario.idFoto }">
									<img src="${ ctx }/verFoto?idFoto=${ forumMensagemBean.mensagem.usuario.idFoto }&key=${ sf:generateArquivoKey(forumMensagemBean.mensagem.usuario.idFoto) }" width="60" height="80"/>
								</c:if>
								<c:if test="${ empty forumMensagemBean.mensagem.usuario.idFoto }">
									<img src="${ ctx }/img/no_picture.png" width="60" height="80"/>
								</c:if>
							</td>
							<td>
								<li>
									<b><h:outputText id="titulo" value="#{ forumMensagemBean.mensagem.titulo }" /></b>
									<br/>
									<i> por </i> <h:outputText id="por" value="#{ forumMensagemBean.mensagem.usuario.nome }" />
									<i> em </i> <h:outputText id="em" value="#{ forumMensagemBean.mensagem.data }"><f:convertDateTime pattern="dd/MM/yyyy HH:mm:ss"/></h:outputText>
								</li>
							</td>
						</tr>
					</table>
				</li>
						
					
				<li>
					<br />
					<p style="text-align: justify;"><h:outputText id="conteudo" value="#{ forumMensagemBean.mensagem.conteudo }" escape="false" /></p>
				</li>
			
			</ul>			
		</fieldset>
		
</rich:modalPanel>
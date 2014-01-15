<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>


<c:set var="confirmRemover" value="if (!confirm('Confirma a remoção desta informação?')) return false" scope="request" />

<f:view>
	
	<h2><ufrn:subSistema /> > Novo Campo <c:if test="${! etiquetaMBean.permissaoAlteracaoTotal}"> Local </c:if> </h2>

<div class="descricaoOperacao"> 
     <p>As informações desta página são usadas na validação dos campos MARC, na parte de catalogação, e também para montar a tela de ajuda desses campos.</p>
     <br/>
     <br/>
     <br/>
     <c:if test="${etiquetaMBean.atualizando}">
	     <p>
		     <strong> OBSERVAÇÃO:  </strong> 
		     	Se os valores dos Subcampos ou dos Indicadores desse campo forem alterados, 
		     	o sistema não permitirá mais serem adicionados nem editados Títulos ou Autoridades com os valores antigos
		     	desse campo.
	     </p>
     </c:if>
</div>

	<a4j:keepAlive beanName="etiquetaMBean"/>
	

	<h:form>

		<table class=formulario width="100%">
		
			<caption class="listagem">Dados do Campo <c:if test="${! etiquetaMBean.permissaoAlteracaoTotal}"> Local </c:if> </caption>
			
			<h:inputHidden value="#{etiquetaMBean.obj.id}" />
			
			<tr>
				<th class="required" width="30%">Tag:</th>
				<td>
					<h:inputText size="3" maxlength="3" value="#{etiquetaMBean.obj.tag}" 
						readonly="#{etiquetaMBean.atualizando}" disabled="#{etiquetaMBean.atualizando}" onkeyup="return formatarInteiro(this);" />
					
					<c:if test="${etiquetaMBean.permissaoAlteracaoTotal}">
						<ufrn:help> Tag de Campos MARC aceitam somente números. Exemplo: 100, 250, 260, entre outras.</ufrn:help>
					</c:if>
					
					<c:if test="${! etiquetaMBean.permissaoAlteracaoTotal}">
						<ufrn:help> Tag de Campos Locais aceitam somente números. Exemplo: 900, 957, 090, entre outras.</ufrn:help>
					</c:if>
					
				</td>	
			</tr>
			
			<tr>
				<th class="required">Descrição da Etiqueta:</th>
				<td>
					<h:inputText size="100" maxlength="80" value="#{etiquetaMBean.obj.descricao}" readonly="#{etiquetaMBean.readOnly}" onkeyup="CAPS(this)" />
				</td>
			</tr>
			
			<tr>
				<th class="required">Pode Repetir?</th>
				<td><h:selectOneRadio value="#{etiquetaMBean.obj.repetivel}">
						<f:selectItem itemLabel="Sim" itemValue="true" />
						<f:selectItem itemLabel="Não" itemValue="false" />
					</h:selectOneRadio> 
					</td>
			</tr>
			
			<tr>
				<th class="required">Tipo de Etiqueta:</th>
				<td><h:selectOneRadio value="#{etiquetaMBean.obj.tipo}">
						<f:selectItem itemLabel="Bibliográfica" itemValue="#{etiquetaMBean.obj.tipoBibliografica}" />
						<f:selectItem itemLabel="Autoridades" itemValue="#{etiquetaMBean.obj.tipoAutoridade}" />
					</h:selectOneRadio> 
					</td>
			</tr>
			
			<tr>
				<th class="required">Descrição do 1º Indicador:</th>
				<td><h:inputText size="40" maxlength="40"
					value="#{etiquetaMBean.obj.descricaoIndicador1}" readonly="#{etiquetaMBean.readOnly}" /></td>
			</tr>
			
			
			
			<tr>
				<th class="required">Descrição do 2º Indicador:</th>
				<td><h:inputText size="40" maxlength="40"
					value="#{etiquetaMBean.obj.descricaoIndicador2}" readonly="#{etiquetaMBean.readOnly}" /></td>
			</tr>
			
			<tr>
				<th>Informação sobre a Etiqueta:</th>
				<td>
					<h:inputTextarea cols="60" rows="2" value="#{etiquetaMBean.obj.info}" readonly="#{etiquetaMBean.readOnly}" />
					<ufrn:help><p>Informações sobre a etiqueta que se jugue necessárias.</p> <p>Aparecem na ajuda do campo.</p></ufrn:help>	
				</td>
			</tr>
			
			<tr>
				<th>Informação sobre o 1º Indicador:</th>
				<td>
					<h:inputTextarea cols="60" rows="2" value="#{etiquetaMBean.obj.infoIndicador1}" readonly="#{etiquetaMBean.readOnly}" />
					<ufrn:help><p>Informações sobre o primeiro indicador que se jugue necessárias.</p> <p>Aparecem na ajuda do campo.</p></ufrn:help>	
				</td>
				
			</tr>
			
			<tr>
				<th>Informação sobre o 2º Indicador:</th>
				<td>
					<h:inputTextarea cols="60" rows="2" value="#{etiquetaMBean.obj.infoIndicador2}" readonly="#{etiquetaMBean.readOnly}" />
					<ufrn:help><p>Informações sobre o segundo indicador que se jugue necessárias.</p> <p>Aparecem na ajuda do campo.</p></ufrn:help>	
				</td>
				
			</tr>
			
			<tr>
				<td colspan="2">
					<div class="infoAltRem" style="margin-top:15px">
						<h:graphicImage value="/img/adicionar.gif" /> 
						<h:commandLink value="Adicionar Indicadores" actionListener="#{etiquetaMBean.novoValorIndicador}" />
						<h:graphicImage value="/img/delete.gif" /> : Remover Indicador
					</div>
				</td>
			</tr>
			
			
			<c:if test="${ fn:length(  etiquetaMBean.obj.valoresIndicadorList) > 0}" > 
				 <tr>
					<td colspan="2">
						<t:dataTable var="valor" value="#{etiquetaMBean.dataModelValores}" style="width: 100%">
							
							<t:column>
								<f:facet name="header"> 
								 	<h:outputText value="Valor"></h:outputText> 
								 </f:facet>
								<span class="required"/> 
								<h:inputText value="#{valor.valor}" size="2" maxlength="1" autocomplete="off">
									<f:attribute name="permiteEspacos" value="true"/>
								</h:inputText>
							</t:column>
							
							<t:column>
								<f:facet name="header">
									<h:outputText value="Descrição do Valor"></h:outputText>
								</f:facet>
								<span class="required"/> <h:inputText value="#{valor.descricao}"/>
							</t:column>
							
							<t:column>
								<f:facet name="header"><h:outputText value="Informação"></h:outputText></f:facet>
								<h:inputTextarea value="#{valor.info}" cols="50" rows="1"/> 
								<ufrn:help><p>Informações sobre o indicador que se jugue necessárias.</p> <p>Aparecem na ajuda do campo.</p></ufrn:help>
							</t:column>
							
							<t:column>
								<f:facet name="header"><h:outputText value="Número do Indicador"></h:outputText></f:facet>
								<span class="required"/> 
								<h:selectOneMenu value="#{valor.numeroIndicador}">
									<f:selectItem itemLabel="1º Indicador" itemValue="#{valor.valorPrimeiro}" />
									<f:selectItem itemLabel="2º Indicador" itemValue="#{valor.valorSegundo}" />
								</h:selectOneMenu>
							</t:column>
							
							<t:column>
								<h:commandLink actionListener="#{etiquetaMBean.removerValorIndicador}" onclick="#{confirmRemover}"> 
									<h:graphicImage value="/img/delete.gif" title="Clique aqui para remover esse indicador"/> 
								</h:commandLink>
							</t:column> 
							
						</t:dataTable>
					</td>
				</tr>
			</c:if>
			
			
			<tr>
				<td colspan="2">
					<div class="infoAltRem" style="margin-top:15px">
						<h:graphicImage value="/img/adicionar.gif" /> 
						<h:commandLink value="Adicionar Subcampos" actionListener="#{etiquetaMBean.novoDescritorSubCampo}" />
						<h:graphicImage value="/img/delete.gif" /> : Remover Subcampo
					</div>
				</td>
			</tr>
			
			
			<c:if test="${ fn:length(  etiquetaMBean.obj.descritorSubCampoList) > 0}"> 
				 <tr>
					<td colspan="2">
						<t:dataTable var="descritor" value="#{etiquetaMBean.dataModelDescritores}" style="width: 100%">
							
							<t:column>
								<f:facet name="header"> 
								 	<h:outputText value="Código"></h:outputText> 
								 </f:facet>
								<span class="required"/> <h:inputText value="#{descritor.codigo}" size="2" maxlength="1"  />
							</t:column>
							
							<t:column>
								<f:facet name="header">
									<h:outputText value="Descrição"></h:outputText>
								</f:facet>
								<span class="required"/> <h:inputText value="#{descritor.descricao}"/>
							</t:column>
							
							<t:column>
								<f:facet name="header"><h:outputText value="Pode Repetir"></h:outputText></f:facet>
								<span class="required"/> 
								<h:selectOneMenu value="#{descritor.repetivel}">
									<f:selectItem itemLabel="Não" itemValue="false" />
									<f:selectItem itemLabel="Sim" itemValue="true" />
								</h:selectOneMenu>
							</t:column>
							
							<t:column>
								<f:facet name="header"><h:outputText value="Informação"></h:outputText></f:facet>
								<h:inputTextarea value="#{descritor.info}" cols="50" rows="1"/> 
								<ufrn:help><p>Informações sobre o subcampo que se jugue necessárias.</p> <p>Aparecem na ajuda do campo.</p></ufrn:help>
							</t:column>
							
							<t:column>
									<h:commandLink actionListener="#{etiquetaMBean.removerDescritorSubCampo}" onclick="#{confirmRemover}"> 
										<h:graphicImage value="/img/delete.gif" title="Clique aqui para remover esse subcampo"/> 
									</h:commandLink>
							</t:column>
							
							
						</t:dataTable>
					</td>
				</tr>
			</c:if>
			
			<tfoot>
				<tr>
					<td colspan="2">
					
					<c:if test="${etiquetaMBean.criando}">
						<h:commandButton value="Cadastrar" action="#{etiquetaMBean.cadastrar}" />
					</c:if>
					
					<c:if test="${etiquetaMBean.atualizando}">
						<h:commandButton value="Atualizar" action="#{etiquetaMBean.atualizar}" />
					</c:if>
					
					<h:commandButton value="Cancelar" immediate="true" action="#{etiquetaMBean.voltarPaginaListagem}"  onclick="#{confirm}" />
					
				</tr>
			</tfoot>
	
		</table>
			
		<center>
			<h:graphicImage url="/img/required.gif" style="vertical-align: top;"/>
			<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
		</center>


	</h:form>
	
</f:view> 


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
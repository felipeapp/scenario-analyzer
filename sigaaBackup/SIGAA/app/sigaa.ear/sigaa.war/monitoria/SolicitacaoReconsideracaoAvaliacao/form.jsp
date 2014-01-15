<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<%@include file="/portais/docente/menu_docente.jsp" %>
	
	<h:outputText value="#{reconsideracaoAvaliacao.create}" />
	<h2><ufrn:subSistema /> > Solicita��o de Reconsidera��o de Projetos de Ensino</h2>

	<table class="formulario" width="100%">
	<caption class="listagem"> Solicita��o de Reconsidera��o de Projetos de Ensino </caption>
	
	<c:if test="${not empty reconsideracaoAvaliacao.projetosReconsideraveisUsuarioLogado}">
	<tr>
    	<td>
			<h:form>
				<h:inputHidden value="#{reconsideracaoAvaliacao.confirmButton}"/>
				<h:inputHidden value="#{reconsideracaoAvaliacao.obj.id}"/>
				<h:inputHidden value="#{reconsideracaoAvaliacao.projeto.id}"/>
		
				<tr>
					<td>
					 	<b>Projeto: </b><projetosReconsideraveish:outputText value="#{reconsideracaoAvaliacao.projeto.titulo}"/><br/>
					</td>
				</tr>
		
				<tr>
					<td>
		
							<t:dataTable value="#{ reconsideracaoAvaliacao.projeto.avaliacoes }" var="avaliacao" width="100%" rowIndexVar="index" forceIdIndex="true">
		
										<t:column>
											<h:outputText value="<b>Avalia��o #{index+1}: </b>" escape="false"/>
											<f:verbatim><br/><b>Data da Avalia��o: </b></f:verbatim><h:outputText value="#{avaliacao.dataAvaliacao}"><f:convertDateTime pattern="dd/MM/yyyy"/></h:outputText>
											<f:verbatim><br/><b>Nota: </b></f:verbatim><h:outputText value="#{avaliacao.notaAvaliacao}"/>
											<f:verbatim><br/><b>Parecer do Avaliador:</b><br/></f:verbatim>
											<h:outputText value="#{avaliacao.parecer}"/>
											<f:verbatim></b><br/></f:verbatim>
										</t:column>
		
							 </t:dataTable>
		
		 	   	</td>
			 </tr>
		
			 <tr>
				<td>
				<hr/>
					<b>Justificativa do Pedido: </b><br/>
					<h:inputTextarea value="#{reconsideracaoAvaliacao.obj.descricao}" rows="15" cols="115" readonly="#{reconsideracaoAvaliacao.readOnly}"/> 
				</td>
			</tr>
		
				<br/>
				<br/>
			
				<tfoot>
					<tr>
						<td colspan="2">

							<c:if test="${reconsideracaoAvaliacao.confirmButton == 'Remover'}">
								<h:commandButton value="#{reconsideracaoAvaliacao.confirmButton}" action="#{reconsideracaoAvaliacao.remover}"/>
							</c:if>
		
							<c:if test="${reconsideracaoAvaliacao.confirmButton != 'Remover'}">
								<h:commandButton value="Pr�ximo >>" action="#{reconsideracaoAvaliacao.reconsiderarProjeto}"/>
							</c:if>
		
		
							<h:commandButton value="Cancelar" action="#{reconsideracaoAvaliacao.cancelar}"/>
						</td>
					</tr>
				</tfoot>
			</h:form>
		</td>
	</tr>
	</c:if>

	<c:if test="${empty reconsideracaoAvaliacao.projetosReconsideraveisUsuarioLogado}">
		<tr>
			<td>
				 <br/>
		         <center><font color="red">N�o h� projetos passivos de reconsidera��o <br/> ou o usu�rio atual n�o � coordenador de projetos ativos</font></center>
		         <br/>
	        </td>
		</tr>
	</c:if>

	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
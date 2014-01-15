<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
	#tableMatriculas .colLeft{text-align: left; }
	#tableMatriculas .colCenter{text-align: center; }
	#tableMatriculas .colRight{text-align: right; }
	
	#tableMatriculasAnteriores .colLeft{text-align: left; }
	#tableMatriculasAnteriores .colCenter{text-align: center; }
	#tableMatriculasAnteriores .colRight{text-align: right; }
	
	
	.colSituacao{ text-align: left; width: 25%; }
	.colFrequencia{ text-align: left; width: 13%; }
	.colConceito{ text-align: left; width: 10%; }
	.colComponente{ text-align: left; width: 40%; }
	.colAnoPeriodo{ text-align: left; width: 10%; }
	
</style>

<f:view>
	
	<h2 class="title">Implantar Histórico > Resumo</h2>

	<c:set var="discente" value="#{implantarHistorico.discente}"/>
	<%@include file="/graduacao/info_discente.jsp"%>
	<br/>



	<h:form prependId="false">
	
	<table class="formulario" width="100%">
	<caption>Matrículas Cadastradas</caption>

	
	
	<c:if test="${not empty implantarHistorico.matriculas}">
	
	<tr><td>

		<table class="subFormulario" width="100%">
			<caption>Matrículas Adicionadas</caption>
		
			<tr><td>
				<h:dataTable value="#{implantarHistorico.matriculasDataModel}" var="mat"
					columnClasses="colCenter, colCenter, colComponente, colRight, colRight, colSituacao"
					rowClasses="linhaPar,linhaImpar" width="100%" id="tableMatriculas">
		
					<c:if test="${implantarHistorico.discente.stricto}">
						<h:column  headerClass="colCenter">
							<f:facet name="header">
								<h:outputText value="Data Inicial"/>
							</f:facet>
							<h:outputText value="#{mat.mes}"/>/<h:outputText value="#{mat.ano}" style="text-align: center;"/>
						</h:column>
						<h:column  headerClass="colCenter">
							<f:facet name="header">
								<h:outputText value="Data Final"/>
							</f:facet>
							<h:outputText value="#{mat.mesFim}"/>/<h:outputText value="#{mat.anoFim}"/>
						</h:column>
					</c:if>
					
					<c:if test="${not implantarHistorico.discente.stricto}">
						<h:column></h:column>
						<h:column  headerClass="colCenter">
							<f:facet name="header"><f:verbatim>Ano-Período</f:verbatim></f:facet>
							<h:outputText value="#{mat.anoPeriodo}"/>
						</h:column>
					</c:if>
		
					<h:column  headerClass="colComponente">
						<f:facet name="header"><f:verbatim>Componente</f:verbatim></f:facet>
						<h:outputText value="#{mat.componente.nome}"/>
					</h:column>
		
					<c:if test="${implantarHistorico.discente.stricto}">
						<h:column headerClass="colRight">
							<f:facet name="header"><f:verbatim>${ implantarHistorico.descricaoMetodoAvaliacao }</f:verbatim></f:facet>
							<h:outputText value="#{ implantarHistorico.conceito ? mat.conceitoChar : implantarHistorico.nota ? mat.mediaFinal : mat.competenciaDescricao }" />
						</h:column>
						
					</c:if>
					<c:if test="${not implantarHistorico.discente.stricto}">
						
						<h:column headerClass="colRight">
							<f:facet name="header"><f:verbatim>${ implantarHistorico.descricaoMetodoAvaliacao }</f:verbatim></f:facet>
							<h:outputText value="#{ implantarHistorico.conceito ? mat.conceitoChar : implantarHistorico.nota ? mat.mediaFinal : mat.competenciaDescricao }" />
						</h:column>
					</c:if>
		
					<h:column  headerClass="colRight">
						<f:facet name="header"><f:verbatim>Frequência</f:verbatim></f:facet>
						<%-- <h:outputText value="#{mat.frequenciaImplantadaHistorico}"/> --%>
						<h:outputText value="#{mat.frequenciaImplantadaHistorico}"/>
					</h:column>
					
					<h:column headerClass="colLeft">
						<f:facet name="header"><f:verbatim>Situação</f:verbatim></f:facet>
						<h:outputText value="#{mat.situacaoMatricula.descricao}"/>
					</h:column>
		
		
				</h:dataTable>
			</td></tr>
		
		</table>

	</td></tr>
	
	
	</c:if>
	
	
	
	<c:if test="${not empty implantarHistorico.matriculasAnteriores}">
	
	 <tr><td>

		<table class="" width="100%">
			<caption>Matrículas Implantadas Anteriormente</caption>
		
			
			<tr><td>
				<h:dataTable value="#{implantarHistorico.matriculasAnterioresDataModel}" var="mat"
					columnClasses="colCenter, colCenter, colComponente, colRight, colRight, colSituacao"
					rowClasses="linhaPar,linhaImpar" width="100%" id="tableMatriculasAnteriores" >
		
					<c:if test="${implantarHistorico.discente.stricto}">
						<h:column  headerClass="colCenter">
							<f:facet name="header">
								<h:outputText value="Data Inicial"/>
							</f:facet>
							<h:outputText value="#{mat.mes}" style="text-align: center;"/>/<h:outputText value="#{mat.ano}" style="text-align: center;"/>
						</h:column>
						<h:column  headerClass="colCenter">
							<f:facet name="header">
								<h:outputText value="Data Final"/>
							</f:facet>
							<h:outputText value="#{mat.mesFim}" style="text-align: center;"/>/<h:outputText value="#{mat.anoFim}"/>
						</h:column>
					</c:if>
					
					<c:if test="${not implantarHistorico.discente.stricto}">
						<h:column></h:column>
						<h:column  headerClass="colCenter">
							<f:facet name="header"><f:verbatim>Ano-Período</f:verbatim></f:facet>
							<h:outputText value="#{mat.anoPeriodo}"/>
						</h:column>
					</c:if>
		
					<h:column  headerClass="colComponente">
						<f:facet name="header"><f:verbatim>Componente</f:verbatim></f:facet>
						<h:outputText value="#{mat.componente.codigoNome}"/>
					</h:column>
		
					<c:if test="${implantarHistorico.discente.stricto}">
						<h:column headerClass="colRight">
							<f:facet name="header"><f:verbatim>${ implantarHistorico.descricaoMetodoAvaliacao }</f:verbatim></f:facet>
							<h:outputText value="#{ implantarHistorico.conceito ? mat.conceitoChar : implantarHistorico.nota ? mat.mediaFinal : mat.competenciaDescricao }" />
						</h:column>
					</c:if>
					
					<c:if test="${not implantarHistorico.discente.stricto}">
						
						<h:column headerClass="colRight">
							<f:facet name="header"><f:verbatim>${ implantarHistorico.descricaoMetodoAvaliacao }</f:verbatim></f:facet>
							<h:outputText value="#{ implantarHistorico.conceito ? mat.conceitoChar : implantarHistorico.nota ? mat.mediaFinal : mat.competenciaDescricao }" />
						</h:column>
					</c:if>
		
					<h:column headerClass="colRight">
						<f:facet name="header"><f:verbatim>Frequência</f:verbatim></f:facet>
						<%-- <h:outputText value="#{mat.frequenciaImplantadaHistorico}"/> --%>
						<h:outputText value="#{mat.frequenciaImplantadaHistorico}"/>
					</h:column>
					
					
					<h:column headerClass="colLeft" >
						<f:facet name="header"><f:verbatim>Situação</f:verbatim></f:facet>
						<h:outputText value="#{mat.situacaoMatricula.descricao}"/>
					</h:column>
		
				</h:dataTable>
			</td></tr>
		
		</table>

	</td></tr> 
	
	</c:if>	






	

	<tr><td>
		<c:set var="exibirApenasSenha" value="true" scope="request"/>
		<%@include file="/WEB-INF/jsp/include/confirma_senha.jsp"%>
	</td></tr>


	<tfoot>
		<tr>
			<td>
				<h:commandButton value="Cadastrar" action="#{implantarHistorico.cadastrar}" id="btnCadastrar"/>
				<h:commandButton value="<< Voltar" action="#{implantarHistorico.voltar}"  id="btnVolta"/>
				<h:commandButton value="Cancelar" action="#{implantarHistorico.cancelar}" id="btnCancelar" onclick="#{confirm}" immediate="true" />
			</td>
		</tr>
	</tfoot>
	</table>

	
			
	</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

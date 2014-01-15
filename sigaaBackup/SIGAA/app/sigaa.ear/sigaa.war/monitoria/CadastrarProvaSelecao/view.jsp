<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	
	<h2>Dados da Prova Seletiva</h2>
	
	<h:form>
	
		<table width="100%" class="tabelaRelatorio">
			<caption class="listagem"> Dados da Prova</caption>
			
			<tbody>
					
					<tr><th>Projeto:</td><td>${provaSelecao.obj.projetoEnsino.titulo}</td></tr>
					<tr><th width="25%">Ano:</td><td>${provaSelecao.obj.projetoEnsino.ano}</td></tr>
					<tr><th>Inscrições até:</td><td><fmt:formatDate pattern="dd/MM/yyyy" value="${provaSelecao.obj.dataLimiteIncricao}"/></td></tr>
					<tr><th>Data da Prova:</td><td><fmt:formatDate pattern="dd/MM/yyyy" value="${provaSelecao.obj.dataProva}"/></td></tr>
					<tr><th>Vagas Remuneradas:</td><td>${provaSelecao.obj.vagasRemuneradas}</td></tr>
					<tr><th>Vagas Não Remuneradas:</td><td>${provaSelecao.obj.vagasNaoRemuneradas}</td></tr>
					<tr><th>Título da Prova:</td><td>${provaSelecao.obj.titulo}</td></tr>
					<tr><th>Situação da Prova:</td><td>${provaSelecao.obj.situacaoProva.descricao}</td></tr>
					
					<tr>
						<td class="subFormulario" colspan="2">Outras Informações:</td>
					</tr>

					<tr>
						<td colspan="2">${provaSelecao.obj.informacaoSelecao}</td>
					</tr>

					<tr>
						<td class="subFormulario" colspan="2">Lista de Requisitos:</td>
					</tr>
					
					<tr>						
						<td colspan="2">
							<t:dataTable id="dtComponentesProva" value="#{provaSelecao.obj.componentesObrigatorios}" var="compProva" 
									align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar" 
									rendered="#{not empty provaSelecao.obj.componentesObrigatorios}">
				
									<t:column>
											<f:facet name="header"><f:verbatim><center>Obrigatório</center></f:verbatim></f:facet>								
											<center><h:outputText value="#{compProva.obrigatorio ? 'SIM':'NÃO'}" /></center>
									</t:column>
									<t:column>
										<f:facet name="header"><f:verbatim>Componente Curricular</f:verbatim></f:facet>
										<h:outputText value="#{compProva.componenteCurricularMonitoria.disciplina.descricao}" />
									</t:column>			
							</t:dataTable>
						</td>
					</tr>
				</tbody>
		</table>
	</h:form>

	<br/>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
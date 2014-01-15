<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<style>
	.colIcone{text-align: right !important;width: 1%;}
	.colPeriodo{text-align: center !important;width: 20%;}
	.footer{text-align: center;}
</style>

<f:view>
	<a4j:keepAlive beanName="inscricaoAtividadeAP"></a4j:keepAlive>
	<h2 class="title"><ufrn:subSistema />  > Remover Participação de Atividade > Seleção de Grupo de Atividade</h2>

	<h:form id="formBuscaParticipante" prependId="true">

		<div class="descricaoOperacao">
			<p>Caro usuário,</p>
			<p>A listagem abaixo exibe todos os grupos de atividades pedagógicas no qual o participante possui atividades.</p>
			<p>Por favor selecione um grupo de atividade para exibição das atividades relacionadas.</p>
		</div>

		<c:if test="${acesso.programaAtualizacaoPedagogica}">
		<table class="visualizacao"  width="80%" >
				<tbody>
				<tr>
					<th width="20%">
					<%-- Docente ou Técnico Administrativo --%>
					<h:outputText value="#{inscricaoAtividadeAP.obj.docente.categoria.descricao}"/>:
					</th>
					<td><h:outputText value="#{inscricaoAtividadeAP.obj.docente.pessoa.nome}"/></td>
				</tr>
				</tbody>
		</table>
		<br/>
		</c:if>
		
		<center>
				<h:messages/>
				<div class="infoAltRem">
				    <h:graphicImage value="/img/seta.gif" style="overflow: visible;"/>: Selecionar Grupo de Atividade
				</div>
		</center>
		
		
		<table class="listagem"  width="80%" >
				<caption>Grupos de Atividades Abertos</caption>
		</table>
		
		<t:dataTable value="#{inscricaoAtividadeAP.gruposDocenteExcluido}" rendered="#{not empty inscricaoAtividadeAP.gruposDocenteExcluido}"
			 var="_g" styleClass="listagem"  width="80%" footerClass="footer"
			 columnClasses="colDocente,colIcone" rowClasses="linhaPar, linhaImpar">
					
					<t:column styleClass="colGrupo" headerstyleClass="colGrupo">
						<f:facet name="header">
							<f:verbatim>Denominação</f:verbatim>
						</f:facet>
						<h:outputText value="#{_g.denominacao}"/>
					</t:column>
					
					<t:column styleClass="colPeriodo" headerstyleClass="colPeriodo">
						<f:facet name="header">
							<f:verbatim>Período de Inscrição</f:verbatim>
						</f:facet>
						<h:outputText value="#{_g.inicioInscricao}"/> à
						<h:outputText value="#{_g.fimInscricao}"/>
					</t:column>
					
					<t:column styleClass="colPeriodo" headerstyleClass="colPeriodo">
						<f:facet name="header">
							<f:verbatim>Período de Atividades</f:verbatim>
						</f:facet>
						<h:outputText value="#{_g.inicio}"/> à
						<h:outputText value="#{_g.fim}"/>
					</t:column>

					<t:column styleClass="colIcone" headerstyleClass="colIcone">
						<f:facet name="header">
							<f:verbatim>&nbsp;</f:verbatim>	
						</f:facet>
			
						<h:commandLink action="#{inscricaoAtividadeAP.selecionarGrupoRemocao}"
								title="Selecionar Grupo de Atividade" id="selecionarGrupo">
							<h:graphicImage value="/img/seta.gif"></h:graphicImage>
							<f:param name="idGrupo" value="#{_g.id}" />
						</h:commandLink>
					</t:column>	
					
					<f:facet name="footer">
						<h:commandButton value="<< Voltar" action="#{inscricaoAtividadeAP.preExcluirGestor}" 
						rendered="#{acesso.programaAtualizacaoPedagogica}"	immediate="true" title="voltar"/>
						<h:commandButton value="Cancelar" action="#{inscricaoAtividadeAP.cancelar}" 
						 onclick="#{confirm}"	immediate="true" title="Cancelar"/>
					</f:facet>
							
		</t:dataTable>
		
		
	</h:form>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<style>
table.formulario th { font-weight: bold; !important}
table.formulario td { text-align: left; }
</style>

<f:view>
<a4j:keepAlive beanName="notificacaoAcademica" />
<a4j:keepAlive beanName="notificacaoAcademicaDiscente" />
	<h:form id="form">
	<h2 class="title"><ufrn:subSistema />
	 > <h:commandLink value="Acompanhar Notificações Acadêmicas" action="#{notificacaoAcademica.voltarAcompanhar}" />
	 > Visualizar Discentes Notificados
</h2>

	<table class="formulario" style="margin-bottom:10px;" cellpadding="5" width="80%">
				<caption class="listagem">Notificação</caption>
				<tbody>
					<tr>
						<th width="25%" style>Descrição:</th>
						<td>
							<h:outputText value="#{ notificacaoAcademicaDiscente.notificacao.descricao }" />
						</td>
					</tr>	
					<tr>
						<th>Texto de Notificação:</th>
						<td>
							<h:outputText value="#{ notificacaoAcademicaDiscente.notificacao.mensagemNotificacao }" escape="false"/>
						</td>
					</tr>
					<tr>
						<th>Exige Confirmação:</th>
						<td>
							<h:outputText value="Sim" rendered="#{ notificacaoAcademicaDiscente.notificacao.exigeConfirmacao }"/>
							<h:outputText value="Não" rendered="#{ !notificacaoAcademicaDiscente.notificacao.exigeConfirmacao }"/>
						</td>
					</tr>
					<tr>
						<th>Total de Confirmadas:</th>
						<td>
							<h:outputText value="#{ notificacaoAcademicaDiscente.totalConfirmadas }"/>
						</td>
					</tr>
					<tr>
						<th>Total de Visualizadas:</th>
						<td>
							<h:outputText value="#{ notificacaoAcademicaDiscente.totalVisualizadas }"/>
						</td>
					</tr>
				</tbody>	
	</table>

	<table class="formulario"  width="80%">
		<caption>Informe os Critérios de Busca</caption>
		<tr>
			<th>	
				&nbsp;<span style="text-align:right;font-weight:normal">Nome do Discente:</span>&nbsp;
			</th>	
			<td>
				<h:inputText id="nomeDiscente" value="#{ notificacaoAcademicaDiscente.discente.pessoa.nome }" size="60"/>
			</td>
		</tr>
		<tr>
			<th>
				&nbsp;<span style="text-align:right;font-weight:normal">Matrícula:</span>&nbsp;
			</th>
			<td>
				<h:inputText value="#{notificacaoAcademicaDiscente.discente.matricula}" size="14" id="matriculaDiscente" maxlength="12" onkeyup="return formatarInteiro(this);" />
			</td>	
		</tr>
		<tr>
			<th>
				&nbsp;<span style="text-align:right;font-weight:normal;">Curso:</span>&nbsp;
			</th>
			<td>
				<h:inputText value="#{notificacaoAcademicaDiscente.discente.curso.nome}" size="60" id="cursoDiscente"/>
			</td>	
		</tr>
		<tfoot><tr><td colspan="3" style="text-align:center;">
			<h:commandButton action="#{notificacaoAcademicaDiscente.filtrarDiscentes}"  value=" Filtrar "/>
			<h:commandButton action="#{notificacaoAcademicaDiscente.exibeTodos}"  value=" Exibir Todos "/>
			<h:commandButton value="<< Voltar"  action="#{notificacaoAcademica.voltarAcompanhar}" />
			</td></tr></tfoot>
	</table><br/>
		
	<c:set var="notificacoes" value="#{ notificacaoAcademicaDiscente.allPaginado }"/>

	<c:if test="${ empty notificacoes }">
		<div style="text-align: center;font-weight:bold;color:red;margin:40px"> 
		Nenhum discente encontrado com os critérios de busca selecionados.
		</div>
	</c:if>	   

	<c:if test="${ not empty notificacoes }">
	
		<div class="infoAltRem">
			<h:graphicImage value="/img/buscar.gif"style="overflow: visible;"/>: Visualizar Informações do Discente
		</div>
			
		<h3 class="tituloTabela">Lista de Discentes (${notificacaoAcademicaDiscente.numNotificacoesDiscentes})</h3>
		<a4j:outputPanel id="discentes" style="margin-top:10px;" >
			<rich:dataTable  var="n" value="#{notificacoes}" rowKeyVar="linha" style="width:100%;">	

					<f:facet name="header">
					<rich:columnGroup>
						<rich:column style="background-color:#ECF4FE">
							<f:verbatim><p align="left">
							<h:outputText value="Matrícula" />
							</p></f:verbatim>
						</rich:column>
						<rich:column style="background-color:#ECF4FE">
							<f:verbatim><p align="left">
							<h:outputText value="Discente" />
							</p></f:verbatim>
						</rich:column>
						<rich:column style="background-color:#ECF4FE">
							<f:verbatim><p align="left">
							<h:outputText value="Confirmada" />
							</p></f:verbatim>
						</rich:column>
						<rich:column style="background-color:#ECF4FE">
							<f:verbatim><p align="left">
							<h:outputText value="Visualizada" />
							</p></f:verbatim>
						</rich:column>
						<rich:column style="background-color:#ECF4FE">
							<f:verbatim><p align="center">
							<h:outputText value="Ultima Visualização" />
							</p></f:verbatim>
						</rich:column>
						<rich:column style="background-color:#ECF4FE">
						</rich:column>
					</rich:columnGroup>
					</f:facet>
				
                 <rich:columnGroup rendered="#{n.mostrarCurso}" style="background-color:#C8D5EC" >
                     <rich:column colspan="6" style="font-weight:bold">
						<h:outputText value="#{ n.discente.curso.nome } / #{ n.discente.curso.municipio.nome } (#{ n.qtdAlunos })"  />
                     </rich:column>
                 </rich:columnGroup>
                 
				<rich:column styleClass="#{ linha % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
					<h:outputText value="#{ n.discente.matricula }" />
				</rich:column>
				<rich:column styleClass="#{ linha % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
					<h:outputText value="#{ n.discente.nome }" />
				</rich:column>
				<rich:column styleClass="#{ linha % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
					<h:outputText value="Sim" rendered="#{ !n.pendente }"  />
					<h:outputText value="Não" rendered="#{ n.pendente }"  />
				</rich:column>	
				<rich:column styleClass="#{ linha % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
					<h:outputText value="Sim" rendered="#{ n.visualizada }"  />
					<h:outputText value="Não" rendered="#{ !n.visualizada }"  />
				</rich:column>	
				<rich:column styleClass="#{ linha % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">	
					<f:verbatim><p align="center">	
					<h:outputText value="#{ n.ultimaVisualizacao }" rendered="#{ n.visualizada }"/>
					<h:outputText value="-" rendered="#{ !n.visualizada }"  />
					</p></f:verbatim>
				</rich:column>
				<rich:column styleClass="#{ linha % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
					<h:commandLink action="#{notificacaoAcademicaDiscente.infoDiscente}" title="Visualizar Informações do Discente">
						<f:param name="idNotificacao" value="#{n.id}"></f:param>
						<h:graphicImage value="/img/buscar.gif" width="16" />
					</h:commandLink>
				</rich:column>	
			</rich:dataTable>	
		</a4j:outputPanel>
	</c:if>

		<div style="text-align: center;"> 
		    <h:commandButton image="/img/voltar.gif" actionListener="#{notificacaoAcademicaDiscente.previousPage}" rendered="#{paginacao.paginaAtual > 0 }"/>
		    <h:selectOneMenu value="#{paginacao.paginaAtual}" valueChangeListener="#{notificacaoAcademicaDiscente.changePage}" rendered="#{paginacao.totalPaginas > 1 }"  onchange="submit()" immediate="true">
				<f:selectItems id="paramPagina" value="#{paginacao.listaPaginas}"/>
		    </h:selectOneMenu>
		    <h:commandButton image="/img/avancar.gif"  actionListener="#{notificacaoAcademicaDiscente.nextPage}"  rendered="#{paginacao.paginaAtual < (paginacao.totalPaginas - 1)}"/>
			<br/>
			<em><h:outputText value="#{paginacao.totalRegistros }"/> Discente(s) Notificado(s)</em>
		</div>
	<div style="position:relative;width:80%;margin:auto;margin-top:5px;border-top:1px solid #CCC;"></div>
	

</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

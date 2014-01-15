<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
<h:messages showDetail="true"></h:messages>

<h:panelGroup id="ajaxErros">
	<h:dataTable  value="#{atividadeExtensao.avisosAjax}" var="msg" rendered="#{not empty atividadeExtensao.avisosAjax}">
		<t:column><h:outputText value="<div id='painel-erros' style='position: relative; padding-bottom: 10px;'><ul class='erros'><li>#{msg.mensagem}</li></ul></div>" escape="false"/></t:column>
	</h:dataTable>
</h:panelGroup>


<%@include file="/portais/docente/menu_docente.jsp"%>
<%@include file="/WEB-INF/jsp/include/errosAjax.jsp"%>


	<h2><ufrn:subSistema /> > Objetivos & Resultados Esperados TUDO</h2>

<div id="divDescricao" class="descricaoOperacao">
	<table width="100%">
		<tr>
			<td width="50%">
			Nesta tela devem ser informados os objetivos e resultados esperados do Projeto.
			</td>
			<td>
				<%@include file="passos_atividade.jsp"%>
			</td>
		</tr>
	</table>
</div>

	<h:form id="frmobjetivos">

		<input type="hidden" name="idObjetivo" value="0" id="idObjetivo"/>
		<input type="hidden" name="id" value="0" id="id"/>
		
		<div class="infoAltRem">
			<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;"/>
			<h:commandLink action="#{ projetoExtensao.cadastrarObjetivo }" value="Cadastrar Objetivo do Projeto"/>
			<h:graphicImage value="/img/alterar.gif" style="overflow: visible;"/>: Alterar Objetivo do Projeto
		    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Objetivo do Projeto
		</div>

		<table class="formulario" width="100%">
			<caption class="listagem">Lista de Objetivos e Resultados Esperados Cadastrados</caption>

				<c:if test="${not empty atividadeExtensao.objetivos}">
					<c:forEach items="#{ atividadeExtensao.objetivos }" var="objetivo" varStatus="status">
							<c:if test="${objetivo.id > 0 }">
								<tr>
									<td colspan="2" align="right" class="subFormulario" >Objetivo ${status.index + 1}</td>
									<td class="subFormulario" width="2%" nowrap="nowrap">
										
										<a4j:commandLink
											actionListener="#{projetoExtensao.carregarObjetivo}"
											reRender="objetivo,quantitativos,qualitativos,atividadesPrincipais,botoes"
											onclick="$('frmobjetivos:objetivo').focus()" rendered="#{objetivo.id > 0 }"
											title="Remover Objetivo do Projeto" >
											<f:param name="idObj" value="#{objetivo.id}" />
											<h:graphicImage url="/img/alterar.gif" />
										</a4j:commandLink>
										&nbsp;
										<h:commandButton image="/img/delete.gif" action="#{projetoExtensao.removeObjetivo}"
													alt="Remover Objetivo do Projeto" title="Remover Objetivo do Projeto" 
													onclick="$(idObjetivo).value=#{objetivo.id};return confirm('Deseja Remover este Objetivo da Ação de Extensão?')"
													rendered="#{objetivo.id > 0 }" />
									</td>
								</tr>
							
								<tr>
									<td colspan="3">
										<table width="100%" id="tbAtividades">
											<thead>
												<tr>
													<th nowrap="nowrap">Objetivos:</th>
													<th nowrap="nowrap">Resultados Quantitativos:&nbsp;&nbsp;</th>
													<th nowrap="nowrap">Resultados Qualitativos:</th>
													<th></th>
												</tr>
											</thead>
											
											<tbody>
												<tr>
													<td valign="top"><h:outputText value="#{objetivo.objetivo}" /></td>
													<td valign="top"><h:outputText value="#{objetivo.quantitativos}" /></td>									
													<td valign="top"><h:outputText value="#{objetivo.qualitativos}" /></td>
													
												</tr>
												<tr>
													<td colspan="3" class="subFormulario">									
														<b>Atividades Relacionadas:</b>
													</td>
												</tr>
												
												<c:forEach items="#{objetivo.atividadesPrincipais}" var="atividade" varStatus="st1">
													<tr>
														<td colspan="2">
															${st1.index + 1}. <h:outputText value="#{atividade.descricao}" />
														</td>
														<td>	
															<h:outputText value="#{atividade.dataInicio}" id="dataInicioAtividade"><f:convertDateTime   pattern="dd/MM/yyyy"  /></h:outputText>
																<c:if test="${not empty atividade.dataFim}">
																	&nbsp; a &nbsp; 
																</c:if> 
															<h:outputText value="#{atividade.dataFim}" id="dataFimAtividade"><f:convertDateTime   pattern="dd/MM/yyyy"  /></h:outputText>						
														</td>
													</tr>		
												</c:forEach>
										
										</table>
									</td>
								</tr>
								
								<tr>
									<td colspan="3"><br/></td>
								</tr>
							</c:if>
					   </c:forEach>
					</c:if>

					<h:outputText value="<tr><td><center><font color='red'>Lista de objetivos vazia</font></center></td></tr>" rendered="#{empty atividadeExtensao.objetivos}" escape="false"/>				

			<tfoot>
				<tr>
					<td colspan="3">
					<h:commandButton value="<< Voltar" action="#{atividadeExtensao.passoAnterior}" id="voltar"/>
					<h:commandButton value="Cancelar" action="#{atividadeExtensao.cancelar}" id="cancelar"  onclick="#{confirm}"/>
					<h:commandButton value="Avançar >>" action="#{projetoExtensao.submeterObjetivos}" id="avancar"/>
					</td>
				</tr>
			</tfoot>
			</table>

		</h:form>
		<br/><center>	<h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span> </center><br/>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
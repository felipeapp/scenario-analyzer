<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<c:set var="confirmDelete" value="if (!confirm('Tem certeza que deseja remover este componente deste curr�culo?')) return false" scope="request"/>
<c:set var="confirmLimpar" value="if (!confirm('Deseja realmente excluir todos os componentes curriculares e/ou o N�vel?')) return false" scope="request"/>
<style type="text/css">
<!--
table.niveis tr td {
	padding: 0px;
}
-->
</style>

<f:view>
	<h2 class="title"><ufrn:subSistema /> > Estrutura Curricular de Matrizes Curriculares &gt; Componentes Curriculares</h2>
	
	<div class="descricaoOperacao">
		<p>
			Esta tela permite adicionar ou remover componentes curriculares da estrutura curricular previamente escolhida.<p />
			Esta estrutura curricular permite o cadastro de no m�ximo ${curriculo.obj.semestreConclusaoIdeal} n�veis. Caso este valor seja ultrapassado, uma mensagem indicando o(s) n�vel(is) excedido(s) ser� mostrada.<p />
			A op��o 'Remover N�vel / Todas as Estruturas Curriculares' remover� apenas as estruturas curriculares para os n�veis normais ou remover�, tamb�m, o n�vel caso este seja o �ltimo excedente da lista.
		</p>
	</div>
	
	<center>
	<div class="infoAltRem">
		<h:form>
			<c:if test="${curriculo.podeAlterarChEObrigatorias}">
				<h:graphicImage value="/img/del_coluna.png" style="overflow: visible;"/>: Remover N�vel / Todas as Estruturas Curriculares
	        </c:if>
	        <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Este Componente do Curr�culo <br/>
			<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>: Adicionar Novo Componente
			<h:graphicImage value="/img/trocar.gif" style="overflow: visible;" />: Alternar entre Obrigat�ria / Optativa
			<h:graphicImage value="/img/substituir.png" style="overflow: visible;" />: Substituir por Outro Componente
	        <br/>
		</h:form>
	</div>
	</center>
	
	<table class="formulario" width="100%">
		<caption class="formulario">Componentes Curriculares</caption>
		<tr>
			<th width="18%" class="rotulo">Carga Hor�ria por Per�odo Letivo:</th>
			<td width="13%">M�nima (${curriculo.obj.chMinimaSemestre} horas)</td>
		</tr>
		<tr>
			<th width="15%" class="rotulo">Cr�ditos por Per�odo Letivo:</th>
			<td width="13%">M�nimo (${curriculo.obj.crMinimoSemestre} crs.)</td>
			<td width="13%">M�dio (${curriculo.obj.crIdealSemestre} crs.)</td>
			<td width="13%">M�ximo (${curriculo.obj.crMaximoSemestre} crs.)</td>
		</tr>
			<tr>
				<th class="rotulo">Carga Hor�ria Optativa (m�n. / total):</th>
				<td>
					<h:outputText id="relacaoChOptativa" value="#{curriculo.obj.chOptativasMinima}/#{curriculo.tmpChOptativa}" />
				</td>
			</tr>
		<tr>
			<th class="rotulo">Carga Hor�ria de Componentes Eletivos:</th>
			<td width="13%">M�xima (${curriculo.obj.maxEletivos} horas)</td>
		</tr>
		<tr>
			<td colspan="4">
				<br>
				<center>
					<div class="infoAltRem">Adicione e/ou Remova Componentes Curriculares deste Curr�culo nos Per�odos Letivos Abaixo
					</div>
				</center>
			</td>
		</tr>
		<tr>
			<td colspan="4">
				<div id="tabs-semestres">
					<h:form id="form">
						<a4j:outputPanel id="tab">
							<rich:tabPanel id="tab_painel" switchType="client" styleClass="niveis" immediate="true" selectedTab="#{curriculo.abaSelecionada}">
								<c:forEach items="#{curriculo.semestres}" var="semestre">
									<rich:tab name="tab#{semestre}" label="#{semestre}�" style="margin: 15%; padding: 3px;" labelWidth="101%" immediate="true">
										<table width="100%">
											<tr>
												<td>
													<h:outputText value="#{semestre}� N�vel" style="font-size: 110%; font-weight: bold;"/>
												</td>
												<c:if test="${curriculo.podeAlterarChEObrigatorias}">
													<td align="right">
														<a4j:commandLink id="limpar" styleClass="noborder" title="Remover N�vel / Todas as Estruturas Curriculares" reRender="tab,chSemestre,relacaoChOptativa"
														actionListener="#{curriculo.limparSemestre}" onclick="#{confirmLimpar}">
															<h:graphicImage value="/img/del_coluna.png" />
															<f:param name="semestre" value="#{semestre}"/>
														</a4j:commandLink>&nbsp;&nbsp;&nbsp;
													</td>
												</c:if>
											</tr>	
										</table>
										<rich:dataTable value="#{curriculo.componentesAdicionados[semestre]}" var="cc" rowClasses="linhaImpar,linhaPar" width="100%">
											<rich:column>
												<h:outputText value="#{cc.componente.descricao}" />
											</rich:column>
											<rich:column>
												<h:outputText value="#{(cc.obrigatoria)?'Obrigat�ria':'Optativa'}" style="font-style: italic; margin-left: 10%; text-align: center;" />
												<a4j:commandLink id="trocarIntegralizacao" title="Alternar entre Optativa / Obrigat�ria" style="float:right; margin-right: 5%;" styleClass="noborder"
													actionListener="#{curriculo.alternarIntegralizacaoOptativaObrigatoria}">
													<h:graphicImage value="/img/trocar.gif" />
													<f:param name="semestre" value="#{semestre}"/>
													<f:param name="idComponente" value="#{cc.componente.id}"/>
												</a4j:commandLink>
											</rich:column>
											<rich:column>
												<c:choose>
													<c:when test="${cc.obrigatoria && !curriculo.podeAlterarChEObrigatorias}">
													</c:when>
													<c:otherwise>
														<center>
															<a4j:commandLink id="remCC" title="Remover Este Componente do Curr�culo" styleClass="noborder"
															actionListener="#{curriculo.removerComponente}" onclick="#{confirmDelete}">
																<h:graphicImage value="/img/delete.gif" />
																<f:param name="semestre" value="#{semestre}"/>
																<f:param name="idComponente" value="#{cc.componente.id}"/>
															</a4j:commandLink>
														</center>
													</c:otherwise>
												</c:choose>
											</rich:column>
											<rich:column>
												<center>
													<h:commandLink id="subs" styleClass="noborder" title="Substituir por Outro Componente"
														action="#{curriculo.escolherComponenteSubstituto}">
															<h:graphicImage value="/img/substituir.png" />
															<f:param name="semestre" value="#{semestre}" />
															<f:param name="idComponente" value="#{cc.componente.id}"/>
													</h:commandLink>
												</center>
											</rich:column>
										</rich:dataTable>
										<table style="background-color: #EFEBDE; width: 100%;">
											<tr>
												<td>
													<h:outputText value="Carga Hor�ria Total: " style="font-size: 105%;" />
													<h:outputText id="chSemestre" value="#{curriculo.chPorSemestre[semestre]}h" style="font-size: 105%; font-weight: bold;" />
												</td>
												<td align="right">
													<h:commandLink id="add" styleClass="noborder" title="Adicionar Novo Componente"
													action="#{curriculo.selecionarComponente}">
														<h:graphicImage value="/img/adicionar.gif" />
														<f:param name="semestre" value="#{semestre}" />
													</h:commandLink>&nbsp;&nbsp;&nbsp;&nbsp;
												</td>
											</tr>
										</table>
									</rich:tab>
								</c:forEach>
							</rich:tabPanel>
						</a4j:outputPanel>
					</h:form>
				</div>
			</td>
		</tr>
		<tfoot>
			<tr>
				<h:form>
				<td colspan="4"><h:commandButton action="#{curriculo.voltarDadosGerais}" 
					value="<< Dados Gerais" /> <h:commandButton id="cancelar" action="#{curriculo.cancelar}" 
					value="Cancelar" onclick="#{confirm}" /> <h:commandButton action="#{curriculo.submeterComponentes}"
					value="Pr�ximo Passo >>" /></td>
			 	</h:form>
			</tr>
		</tfoot>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

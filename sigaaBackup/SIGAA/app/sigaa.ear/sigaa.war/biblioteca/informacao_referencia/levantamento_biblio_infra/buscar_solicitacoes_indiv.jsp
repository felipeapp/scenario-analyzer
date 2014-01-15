<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<h2><ufrn:subSistema/> &gt; Solicitações de Levantamento Bibliográfico</h2>

	<div class="descricaoOperacao">
		Abaixo estão listadas as solicitações de levantamento bibliográfico. Utilize os filtros disponíveis
		para refinar a busca de solicitações.
	</div>

	<h:form id="listagem">
	
		<table class="formulario">
			<caption>Buscar Solicitações</caption>
		
			<tbody>
				<tr>
					<th>Biblioteca:</th>
					<td>
						<h:selectOneMenu value="#{levantamentoBibliograficoInfraMBean.biblioteca.id}" id="biblioteca">
							<ufrn:checkRole papel="<%= SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL %>">
								<f:selectItem itemLabel="Todas" itemValue="0" />
							</ufrn:checkRole>
							<f:selectItems value="#{levantamentoBibliograficoInfraMBean.bibliotecasDeBuscaCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
				
				<tr>
					<th>Situação:</th>
					<td>
						<h:selectOneMenu value="#{levantamentoBibliograficoInfraMBean.situacao}" id="situacao">
							<f:selectItem itemLabel="Não finalizadas" itemValue="-1" />
							<f:selectItem itemLabel="Todas" itemValue="0" />
							<f:selectItem itemLabel="Aguardando Validação" itemValue="1" />
							<f:selectItem itemLabel="Validada" itemValue="2" />
							<f:selectItem itemLabel="Finalizada" itemValue="3" />
						</h:selectOneMenu>
					</td>
				</tr>
				
				<tr>
					<th>Infra?</th>
					<td>
						<h:selectOneRadio value="#{levantamentoBibliograficoInfraMBean.infra}" id="infra" >
							<f:selectItem itemLabel="Ambas" itemValue="0" />
							<f:selectItem itemLabel="Sim" itemValue="1" />
							<f:selectItem itemLabel="Não" itemValue="2" />
						</h:selectOneRadio>
					</td>
				</tr>
			</tbody>
			
			<tfoot>
				<tr><td colspan="2">
					<h:commandButton
							action="#{levantamentoBibliograficoInfraMBean.listarSolicitacoesLevantFuncBiblioteca}"
							value="Buscar" id="buscar"/>
				</td></tr>
			</tfoot>
		</table>

		<div class="infoAltRem" style="margin-top: 5px;">
			<h:graphicImage value="/img/avaliar.gif" style="overflow: visible;"/>: Visualizar / Finalizar Solicitação
			<h:graphicImage value="/img/check.png" style="overflow: visible;"/>: Validar Solicitação
			<h:graphicImage value="/img/refresh.png" style="overflow: visible;"/>: Enviar para outra Biblioteca
		</div>
		
		<table class="listagem" width="80%" >
			<caption>Solicitações</caption>
	
			<thead>
				<tr>
				<th style="text-align:center;width:100px;">Data da Solicitação</th>
				<th>Solicitante</th>
				<th>Biblioteca</th>
				<th style='text-align:center;'>Infra?</th>
				<th style='text-align:center;'>Situação</th>
				<th width="20"></th>
				<th width="20"></th>
				<th width="20"></th>
			</thead>
		
			<tbody>
				<c:forEach items="#{levantamentoBibliograficoInfraMBean.solicitacoes}" var="s" varStatus="loop">
					
					<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td style="text-align:center;">
							<ufrn:format type="data" valor="${s.dataSolicitacao}"/> 
						</td>
						
						<td>
							${s.pessoa.nome}
						</td>
						
						<td>
							${s.bibliotecaResponsavel.descricao}
						</td>
						
						<td style='text-align:center;'>
							${s.infra ? "Sim" : "Não"}
						</td>
						
						<td style='text-align:center;'>
							${s.descricaoSituacao}
						</td>
						
						<td>
							<c:if test="${s.situacao != 3}">
								<h:commandLink action="#{levantamentoBibliograficoInfraMBean.preTransferirSolicitacao}">
									<f:param name="id" value="#{s.id}"/>
									<h:graphicImage value="/img/refresh.png" title="Enviar para outra Biblioteca" />
								</h:commandLink>
							</c:if>
						</td>
	
						<td>
							<c:if test="${s.situacao == 1}">
								<h:commandLink action="#{levantamentoBibliograficoInfraMBean.validarSolicitacao}"
										onclick="if (!confirm(\"Confirma a validação desta solicitação?\")) return false;">
									<f:param name="id" value="#{s.id}"/>
									<h:graphicImage value="/img/check.png" title="Validar Solicitação" />
								</h:commandLink>
							</c:if>
						</td>
						<td>
							<c:if test="${s.situacao != 3}">
								<h:commandLink action="#{levantamentoBibliograficoInfraMBean.preFinalizar}">
									<f:param name="id" value="#{s.id}"/>
									<h:graphicImage value="/img/avaliar.gif" title="Visualizar / Finalizar Solicitação" />
								</h:commandLink>
							</c:if>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>

	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
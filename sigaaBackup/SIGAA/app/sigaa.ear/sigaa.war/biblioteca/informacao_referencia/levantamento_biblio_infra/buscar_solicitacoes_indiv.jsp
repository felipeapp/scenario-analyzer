<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<h2><ufrn:subSistema/> &gt; Solicita��es de Levantamento Bibliogr�fico</h2>

	<div class="descricaoOperacao">
		Abaixo est�o listadas as solicita��es de levantamento bibliogr�fico. Utilize os filtros dispon�veis
		para refinar a busca de solicita��es.
	</div>

	<h:form id="listagem">
	
		<table class="formulario">
			<caption>Buscar Solicita��es</caption>
		
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
					<th>Situa��o:</th>
					<td>
						<h:selectOneMenu value="#{levantamentoBibliograficoInfraMBean.situacao}" id="situacao">
							<f:selectItem itemLabel="N�o finalizadas" itemValue="-1" />
							<f:selectItem itemLabel="Todas" itemValue="0" />
							<f:selectItem itemLabel="Aguardando Valida��o" itemValue="1" />
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
							<f:selectItem itemLabel="N�o" itemValue="2" />
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
			<h:graphicImage value="/img/avaliar.gif" style="overflow: visible;"/>: Visualizar / Finalizar Solicita��o
			<h:graphicImage value="/img/check.png" style="overflow: visible;"/>: Validar Solicita��o
			<h:graphicImage value="/img/refresh.png" style="overflow: visible;"/>: Enviar para outra Biblioteca
		</div>
		
		<table class="listagem" width="80%" >
			<caption>Solicita��es</caption>
	
			<thead>
				<tr>
				<th style="text-align:center;width:100px;">Data da Solicita��o</th>
				<th>Solicitante</th>
				<th>Biblioteca</th>
				<th style='text-align:center;'>Infra?</th>
				<th style='text-align:center;'>Situa��o</th>
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
							${s.infra ? "Sim" : "N�o"}
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
										onclick="if (!confirm(\"Confirma a valida��o desta solicita��o?\")) return false;">
									<f:param name="id" value="#{s.id}"/>
									<h:graphicImage value="/img/check.png" title="Validar Solicita��o" />
								</h:commandLink>
							</c:if>
						</td>
						<td>
							<c:if test="${s.situacao != 3}">
								<h:commandLink action="#{levantamentoBibliograficoInfraMBean.preFinalizar}">
									<f:param name="id" value="#{s.id}"/>
									<h:graphicImage value="/img/avaliar.gif" title="Visualizar / Finalizar Solicita��o" />
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
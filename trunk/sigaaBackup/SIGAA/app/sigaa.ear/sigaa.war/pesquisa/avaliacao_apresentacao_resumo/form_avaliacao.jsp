<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h:messages showDetail="true"></h:messages>
	<h2><ufrn:subSistema /> &gt; Ficha de Avaliação de Apresentação de Resumo do CIC</h2>
	<h:outputText value="#{avaliacaoApresentacaoResumoBean.create}" />
	<h:form id="form">
		<table class="formulario" width="100%">
			<caption>Ficha de Avaliação</caption>
			<tbody>
				
				<tr>
					<td class="subFormulario" colspan="2">1. Identificação do(a) Aluno(a)</td>
				</tr>
				<c:set var="resumo" value="${avaliacaoApresentacaoResumoBean.avaliacao.resumo}" />
				<tr>
					<td>
						<b>Nº Painel / Código Resumo</b>
					</td>
					<td>
						<b>Nome do Aluno(a)</b>
					</td>
				</tr>
				<tr>
					<td> ${ resumo.numeroPainel } / ${ resumo.codigo }</td>
					<td> ${ resumo.autor.nome } </td>
				</tr>
				<tr>
					<td>
						<b>Curso</b>
					</td>
					<td>
						<b>Nome do Orientador(a)</b>
					</td>
				</tr>
				<tr>
					<td> ${ resumo.autor.discente.curso.descricao } </td>
					<td> ${ resumo.orientador.nome } </td>
				</tr>
				<tr>
					<td class="subFormulario" colspan="2">2. Avaliação</td>
				</tr>
				<tr>
					<td colspan="2">
						<t:dataTable value="#{avaliacaoApresentacaoResumoBean.avaliacao.notasItens}" var="notaItem" id="notas" rowIndexVar="index" width="100%" rowClasses="linhaPar, linhaImpar" styleClass="listagem">
							<t:column>
								<f:facet name="header">
									<f:verbatim>Itens da Avaliação</f:verbatim>
								</f:facet>
								<h:outputText value="#{notaItem.itemAvaliacao.descricao}" />
							</t:column>
							<t:column>
								<f:facet name="header">
									<f:verbatim>Conceitos</f:verbatim>
								</f:facet>
								<h:selectOneRadio value="#{notaItem.nota}">
									<f:selectItem itemLabel="Excelente" itemValue="5.0"/>
									<f:selectItem itemLabel="Bom" itemValue="4.0"/>
									<f:selectItem itemLabel="Regular" itemValue="3.0"/>
									<f:selectItem itemLabel="Fraco" itemValue="2.0"/>
								</h:selectOneRadio>
							</t:column>
						</t:dataTable>
					</td>
				</tr>

				<tr>
					<td class="subFormulario" colspan="2">3. Workshop</td>
				</tr>
				<tr>
					<td>Este trabalho está apto para ir ao workshop?</td> 
					<td>
						<h:selectOneRadio value="#{avaliacaoApresentacaoResumoBean.avaliacao.selecionadoApresentacaoOral}">
							<f:selectItem itemLabel="Sim" itemValue="true"/>
							<f:selectItem itemLabel="Não" itemValue="false"/>
						</h:selectOneRadio>
					</td>
				</tr>

				<tr>
					<td class="subFormulario" colspan="2">4. Identificação do Professor(a) avaliador(a)</td>
				</tr>
				<tr>
					<td> Espaço reservado às observações: </td>
				</tr>
				<tr>
					<td colspan="2" style="text-align: center"> 
						<h:inputTextarea id="observacoes" value="#{avaliacaoApresentacaoResumoBean.avaliacao.observacoes}" cols="2" rows="7" style="width: 95%" />
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
				<td colspan="2">
					<h:commandButton value="Confirmar Avaliação" action="#{avaliacaoApresentacaoResumoBean.avaliarResumo}" /> 
					<h:commandButton value="<< Voltar" action="#{avaliacaoApresentacaoResumoBean.listarResumosAvaliador}" rendered="#{!avaliacaoApresentacaoResumoBean.pesquisa}"/>
					<h:commandButton value="<< Voltar" action="#{avaliacaoApresentacaoResumoBean.listarResumosGestor}" rendered="#{avaliacaoApresentacaoResumoBean.pesquisa}"/>
					<h:commandButton value="Cancelar" action="#{avaliacaoApresentacaoResumoBean.cancelar}" onclick="#{confirm}" />
				</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
		
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

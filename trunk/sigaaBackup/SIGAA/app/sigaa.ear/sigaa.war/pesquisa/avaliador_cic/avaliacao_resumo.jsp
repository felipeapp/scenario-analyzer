<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

	<h2><ufrn:subSistema /> &gt; Avaliação de Resumo do CIC</h2>
	<h:outputText value="#{avaliacaoResumoBean.create}" />
	<h:form id="form">
		<table class="formulario" width="100%">
			<caption>Dados para a Avaliação</caption>
			<tbody>
				
				<tr>
					<td class="subFormulario" colspan="2">Corpo do Resumo</td>
				</tr>
				<c:set var="resumo" value="${avaliacaoResumoBean.avaliacao.resumo}" />
				<tr>
					<td width="15%" style="text-align: right;">
						<b>Autor:</b>
					</td>
					<td>
						${ resumo.autor.nome }
						<c:if test="${acesso.pesquisa}">
						 <em> (CPF: <ufrn:format type="cpf_cnpj" name="resumo" property="autor.cpf" />) </em>
						 </c:if>
					</td>
				</tr>
				<tr>
					<td style="text-align: right;">
						<b>Orientador:</b>
					</td>
					<td> ${ resumo.orientador.nome } </td>
				</tr>
			
				<c:if test="${ not empty resumo.coAutores}">
				<c:set var="coAutor_" value="${fn:length(resumo.autores)}" />  
					<tr>
						<td style="text-align: right;" rowspan="${coAutor_ - 1}">
							<b>Co-autor(es): </b><br/>
						</td>
						<td style="text-align: left;">
								<c:forEach var="autor" items="${ resumo.autores }" varStatus="loop">
									<c:if test="${ autor.coAutor }">
									<tr>
										<td style="text-align: left;">${ autor.nome }</td>
									</tr>
									</c:if>
								</c:forEach>
						</td>
					</tr>
				</c:if>
			
				<tr>
					<td colspan="2">
						<b>Área de Conhecimento: </b>
					</td>
				</tr>
				<tr>
					<td colspan="2" class="campo">
						${ resumo.areaConhecimentoCnpq.nome }
						</td>
					</tr>
					<tr>
						<td colspan="2">
							<b>Título</b>
						</td>
					</tr>
					<tr>
						<td colspan="2" class="campo">
							<ufrn:format name="resumo" property="titulo" type="texto"/>
						</td>
					</tr>
					<tr>
						<td colspan="2">
							<b>Resumo</b>
						</td>
					</tr>
					<tr>
						<td colspan="2" class="campo">
							<ufrn:format name="resumo" property="resumo" type="texto"/>
						</td>
					</tr>
					<tr>
						<td colspan="2">
							<b>Palavras-Chave</b>
						</td>
					</tr>
					<tr>
						<td colspan="2" class="campo">
							<ufrn:format name="resumo" property="palavrasChave" type="texto"/>
						</td>
					</tr>
				<tr>
					<td class="subFormulario" colspan="2">Dados da Avaliação</td>
				</tr>
				<tr>
					<th> <h:selectBooleanCheckbox id="erroPortugues" value="#{avaliacaoResumoBean.avaliacao.erroPortugues}"/> </th>
					<td>Possui erros gramaticais?</td>
				</tr>
				<tr>
					<th> <h:selectBooleanCheckbox id="erroConteudo" value="#{avaliacaoResumoBean.avaliacao.erroConteudo}"/> </th>
					<td>Possui erros de conteúdo?</td>
				</tr>
				<tr>
					<th class="required">Parecer:</th>
					<td> <h:inputTextarea id="parecer" value="#{avaliacaoResumoBean.avaliacao.parecer}" cols="2" style="width: 95%" /> </td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
				<td colspan="2">
					<h:commandButton value="Confirmar Avaliação" action="#{avaliacaoResumoBean.avaliarResumo}" /> 
					<h:commandButton value="<< Voltar" action="#{avaliacaoResumoBean.listarResumos}" />
					<h:commandButton value="Cancelar" action="#{avaliacaoResumoBean.cancelar}" onclick="#{confirm}" />
				</td>
				</tr>
			</tfoot>
		</table>
	</h:form>

<br>
<center>
<h:graphicImage url="/img/required.gif" style="vertical-align: top;"/>
<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
</center>
<br>
		
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
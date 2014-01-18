<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<a4j:keepAlive beanName = "selecionaDiscenteMBean" />
<h2> <ufrn:subSistema /> &gt;Movimenta��o dos Discente</h2>

	<div class="descricaoOperacao">
		Caro Usu�rio,<br/> 
		Essa opera��o tem como objetivo estornar algum afastamento realizado de forma errada. O usu�rio seleciona uma movimenta��o do discente, e por exemplo no caso do estorno de um trancamento de programa, suas matr�culas s�o recuperadas e o usu�rio  
		retorna ao status de ATIVO. 
	</div>
	
	<h:form  id="formulario">
	
	
			<br/>
			<c:set var="discente_" value="#{estornaOperacaoRedeMBean.obj.discente}"/>
			<c:set var="_largura" value="70%" />
			<%@include file="/ensino_rede/discente/resumo_dados_discente.jsp"%>
	
			<br/>
			<br/>
			
			<div class="infoAltRem" style="width:70%">
				<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />:  Selecionar  Movimenta��o<br />
			</div>
			
			<table class="listagem" style="width:70%">
			  <caption>Movimenta��es Encontradas (${fn:length(estornaOperacaoRedeMBean.resultadosBusca)})</caption>
				<thead>
					<tr>
						<th>Tipo</th>
						<th>Ano-Per�odo</th>
						<th></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="linha" items="#{estornaOperacaoRedeMBean.resultadosBusca}" varStatus="status">
						<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
							<td>
								<c:if test="${ linha.dataRetorno != null }">
									RETORNO DE
								</c:if> 
								${linha.tipo.descricao }
							</td>
							<td> 
								${linha.anoReferencia } - ${linha.periodoReferencia }
							</td>
							<td align="right" width="2%">
								<h:commandLink action="#{estornaOperacaoRedeMBean.selecionarMovimentacao}" id="selecionarMovimentacao" title="Selecionar Movimenta��o">
									<f:param name="idMovimentacao" value="#{linha.id}" />
									<h:graphicImage url="/img/seta.gif" alt="Selecionar Movimenta��o" />
								</h:commandLink>
							</td>
						</tr>
					</c:forEach>
					
				</tbody>
				
					
		</tbody>
					
	</table>
	<table class="formulario" style="width:70%"> 
		<tfoot>
			<tr><td colspan="2">
				<h:commandButton value="<< Voltar" action="#{ selecionaDiscenteMBean.voltar }" id="voltarMov"/>
				<h:commandButton value="Cancelar" action="#{ estornaOperacaoRedeMBean.cancelar }" onclick="return confirm('Deseja cancelar a opera��o?');" id="btnCancelar"/>
			</td></tr>
		</tfoot>
	</table>	
			
			
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
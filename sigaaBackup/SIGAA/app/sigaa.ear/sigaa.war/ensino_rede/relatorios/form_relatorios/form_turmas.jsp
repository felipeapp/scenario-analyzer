<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<c:set var="confirmSimples"
		value="return confirm('Deseja cancelar a opera��o?');"
		scope="request" />

<f:view>
	<h2><ufrn:subSistema /> &gt; Busca Relat�rio </h2>

	<h:form id="formulario">
		<table class="formulario" style="width:65%;">
			<caption> Informe os crit�rios de busca</caption>
				
				<c:if test="${ relatoriosEnsinoRedeMBean.filtrarUnidade }">
					<tr>
						<td style="width:15%; text-align: right;"> Unidade: </td>
						<td>
							<h:selectManyMenu value="#{ relatoriosEnsinoRedeMBean.dados.filtrarUnidade }" style="height: 80px; width: 100%;">
								<f:selectItems value="#{ relatoriosEnsinoRedeMBean.unidades }"/>
							</h:selectManyMenu>
						</td>
					</tr>
				</c:if>
				
				<c:if test="${ relatoriosEnsinoRedeMBean.filtrarAnoPeriodo }">
					<tr>
						<td style="width:15%; text-align: right;"> Ano Per�odo: </td>
						<td>
							<h:inputText id="versaoz" value="#{relatoriosEnsinoRedeMBean.dados.filtrarAnoPeriodo[0]}" size="5" 
								onkeypress="return(formatarMascara(this,event,'####.#'))" maxlength="6"/>
						</td>
					</tr>
				</c:if>

				<c:if test="${ relatoriosEnsinoRedeMBean.filtrarDisciplina }">
					<tr>
						<td style="width:15%; text-align: right;"> Disciplina: </td>
						<td>
							<h:selectManyMenu value="#{ relatoriosEnsinoRedeMBean.dados.filtrarDisciplina }" style="height: 80px; width: 100%;">
								<f:selectItems value="#{ relatoriosEnsinoRedeMBean.componentesPrograma }"/>
							</h:selectManyMenu>
						</td>
					</tr>
				</c:if>

			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton action="#{ relatoriosEnsinoRedeMBean.gerarRelatorioTurma }" value="Buscar" id="buscar" />
						<h:commandButton action="#{ relatoriosEnsinoRedeMBean.cancelar }" value="Cancelar" id="cancelar" onclick="#{confirmSimples}" />
					</td>
				</tr>
			</tfoot>
			
		</table>
	</h:form>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
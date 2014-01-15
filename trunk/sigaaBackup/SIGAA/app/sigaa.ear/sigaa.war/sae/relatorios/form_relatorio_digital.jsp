<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

<a4j:keepAlive beanName="relatoriosDigitalMBean" />
	
<c:if test="${relatoriosDigitalMBean.exibirDiscentesComDigital}">
	<h2><ufrn:subSistema/> > Relat�rio de Discentes Contemplados Com Digitais</h2>
</c:if>

<c:if test="${!relatoriosDigitalMBean.exibirDiscentesComDigital}">
	<h2><ufrn:subSistema/> > Relat�rio de Discentes Contemplados Sem Digitais </h2>
</c:if>

	<h:form id="form">
		<table class="formulario" style="width: 95%">
			<caption>Informe o Ano/Per�odo</caption>

			<tr>
				<th class="required">Ano: </th>
				<td>
					<h:inputText id="ano" value="#{relatoriosDigitalMBean.ano}" size="5" maxlength="4" 
						 onkeyup="return formatarInteiro(this);" />
				</td>
			</tr>
			<tr>
				<th class="required">Per�odo: </th>
				<td>
					<h:inputText id="semestre" value="#{relatoriosDigitalMBean.periodo}" size="2" maxlength="1" 
						 onkeyup="return formatarInteiro(this);" />
				</td>
			</tr>
			
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton action="#{relatoriosDigitalMBean.gerarRelatorioDiscentesContempladosSAEComDigital}" value="Emitir Relat�rio" rendered="#{relatoriosDigitalMBean.exibirDiscentesComDigital}"/>
						<h:commandButton action="#{relatoriosDigitalMBean.gerarRelatorioDiscentesContempladosSAESemDigital}" value="Emitir Relat�rio" rendered="#{!relatoriosDigitalMBean.exibirDiscentesComDigital}"/>
						<h:commandButton value="Cancelar" action="#{relatoriosDigitalMBean.cancelar}" id="cancelar" 
							onclick="return confirm('Deseja cancelar a opera��o? Todos os dados digitados n�o salvos ser�o perdidos!');"> 
						</h:commandButton>
					</td>
				</tr>
			</tfoot>
			</table>
		</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
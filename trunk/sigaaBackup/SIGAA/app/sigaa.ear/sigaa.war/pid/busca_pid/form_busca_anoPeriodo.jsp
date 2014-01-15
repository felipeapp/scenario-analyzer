<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<a4j:keepAlive beanName="consultaPidMBean"></a4j:keepAlive>

<h2><ufrn:subSistema/> > Exportação de Dados do PID </h2>

	<h:form id="form" >
	<table class="formulario" style="width: 400px">
		<caption>Informe o Ano Período</caption>
		<tr >
			<td style="text-align: center;" >Ano-Período:
			
				<h:inputText id="ano" 
					value="#{consultaPidMBean.ano}"
					onkeyup="return formatarInteiro(this);" 
					onfocus="$('form:opcaoAnoPeriodo').checked = true;" size="4" maxlength="4" /> . 
				
				<h:inputText id="periodo" 
					value="#{consultaPidMBean.periodo}" 
					onkeyup="return formatarInteiro(this);" 
					onfocus="$('form:opcaoAnoPeriodo').checked = true;" size="1" maxlength="1" />
			</td>
		</tr>
		<tfoot>
			<tr>
				<td >
					<h:commandButton action="#{consultaPidMBean.exportarPID}" value="Buscar" />
					<h:commandButton action="#{consultaPidMBean.cancelar}" value="Cancelar" onclick="#{confirm}" immediate="true" />
				</td>
			</tr>
		</tfoot>
		
	</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
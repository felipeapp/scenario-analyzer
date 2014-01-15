<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<jwr:style src="/css/ensino/pid.css" media="all"/>

<h2><ufrn:subSistema /> > Plano Individual do Docente</h2>

<script type="text/javascript">
	function qteCaracteresObsGeral(){	
				
		if ($('form:observacaoGeralChefe').value.length < 500)
			$('txtCaracteresDigitadosGeral').innerHTML=$('form:observacaoGeralChefe').value.length + ' digitados';
		else{
			$('form:observacaoGeralChefe').value = $('form:observacaoGeralChefe').value.substr(0,500);
			$('txtCaracteresDigitadosGeral').innerHTML= '500 digitados';
		}			
	}
</script>

<f:view>
   	<%@include file="/WEB-INF/jsp/include/errosAjax.jsp"%>
   	<%@include file="/pid/panel.jsp"%>
   	
	<a4j:keepAlive beanName="cargaHorariaPIDMBean"></a4j:keepAlive>
				
	<h:form id="form">
		<c:set var="_pidBean" value="#{cargaHorariaPIDMBean}" />
		<%@include file="/pid/_painel_identificacao.jsp"%>
		<%@include file="/pid/busca_pid/_dados_pid_relatorio.jsp"%>
		<%@include file="/pid/_painel_resumo.jsp"%>
		
		<br>
		
		<ufrn:checkNotRole papeis="<%= new int[] { SigaaPapeis.CHEFE_DEPARTAMENTO } %>">
			<c:if test="${ not empty cargaHorariaPIDMBean.obj.observacaoChefeDepartamento }">  	
				<rich:panel header="COMENTÁRIOS FEITOS PELO CHEFE DO DEPARTAMENTO SOBRE O PID" id="observacaoChefe" styleClass="painelAtividades">
				
					<rich:column styleClass="esquerda">
						<h:inputTextarea id="observacaoGeralChefeView" value="#{cargaHorariaPIDMBean.obj.observacaoChefeDepartamento}" cols="132" rows="5" 
						disabled="true" />
						<br/> 
					</rich:column>
					
				</rich:panel>
			</c:if>
		</ufrn:checkNotRole>
				
		<br>
		
		<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.CHEFE_DEPARTAMENTO } %>">  	
			<rich:panel header="COMENTÁRIOS" styleClass="painelAtividades" rendered="#{cargaHorariaPIDMBean.readOnly==false}">
			
				<rich:column styleClass="esquerda">
					Caso deseje enviar algum comentário/observação para o docente desse PID informe abaixo.  
					<h:inputTextarea id="observacaoGeralChefe" value="#{cargaHorariaPIDMBean.obj.observacaoChefeDepartamento}" cols="132" rows="5" 
						onkeyup="qteCaracteresObsGeral()" 
						onblur="qteCaracteresObsGeral()" />
					<br/>  <center><i>(500 caracteres/<span id="txtCaracteresDigitadosGeral">0 digitados</span> )</i></center> 
				</rich:column>
				
			</rich:panel>
		</ufrn:checkRole>
		
		<br>
		
		<table class="formulario" width="100%" id="tabela">
			<tfoot>
				<tr>
					<td colspan="2" align="center">
						
						<h:commandButton value="Homologar" action="#{cargaHorariaPIDMBean.homologarPID}" id="homologar" 
							onclick="return(confirm('Tem certeza que deseja HOMOLOGAR o PID desse docente?'));" rendered="#{cargaHorariaPIDMBean.readOnly==false}" />
						
						<h:commandButton value="Retornar para alterações" action="#{cargaHorariaPIDMBean.recusarPID}" id="recusar" 
							onclick="return(confirm('Tem certeza que deseja RETORNAR o PID desse docente?'));" rendered="#{cargaHorariaPIDMBean.readOnly==false}" />
						
						<h:commandButton value="#{cargaHorariaPIDMBean.confirmButton}" action="#{cargaHorariaPIDMBean.gerarListagemPIDDocente}" rendered="#{cargaHorariaPIDMBean.readOnly==true}" id="voltarDocente" />
						<h:commandButton value="Cancelar" action="#{cargaHorariaPIDMBean.gerarListagemPIDChefeDepartamento}" rendered="#{cargaHorariaPIDMBean.readOnly==false}" id="voltarChefia" onclick="return(confirm('Tem certeza que deseja cancelar esta operação?'));"/>
					</td>
				</tr>
			</tfoot>
		</table>
		
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
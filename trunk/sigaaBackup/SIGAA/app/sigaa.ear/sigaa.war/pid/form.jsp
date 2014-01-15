<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<jwr:style src="/css/ensino/pid.css" media="all"/>
<style>

</style>

<script type="text/javascript">
	function qteCaracteresObsGeral(){	
				
		if ($('form:observacaoGeral').value.length < 5000)
			$('txtCaracteresDigitadosGeral').innerHTML=$('form:observacaoGeral').value.length + ' digitados';
		else{
			$('form:observacaoGeral').value = $('form:observacaoGeral').value.substr(0,5000);
			$('txtCaracteresDigitadosGeral').innerHTML= '5000 digitados';
		}			
	}
</script>

<f:view>
 <%@include file="/WEB-INF/jsp/include/errosAjax.jsp"%>
   	
<h2><ufrn:subSistema /> > Plano Individual do Docente > Formulário</h2>

     
	<div class="descricaoOperacao">
		<h5> Caro(a) Professor(a), </h5>
		<p>
			Este formulário contém os campos necessários para o preenchimento do seu Plano Individual Docente, sendo que algumas das informações foram populadas previamente a partir dos dados existentes no sistema acadêmico. 
			Será solicitado que algumas cargas horárias sejam informadas, bem como atividades específicas a serem desenvolvidas no período de referência deste plano.
		</p>
		<p>
			É possível salvar os dados informados para posterior término do preenchimento, mas  <strong> para que seja válido o PID deve ser submetido à chefia de sua unidade, para que seja realizada a homologação</strong>. 
			Após a submissão à chefia o plano ainda poderá ser alterado, mas voltará a ser necessária sua homologação. 
		</p>
		<p> <ufrn:manual codigo="2_12000_1" /> </p>
	</div>
	
   	<%@include file="/pid/panel.jsp"%>
   	
	<a4j:keepAlive beanName="cargaHorariaPIDMBean"></a4j:keepAlive>
				
	<h:form id="form">
 		
 		<c:set var="_pidBean" value="#{cargaHorariaPIDMBean}" />
 		<%@include file="/pid/_painel_identificacao.jsp"%>
 		<%@include file="/pid/_painel_ensino.jsp"%>
 		<%@include file="/pid/_painel_atividades_academicas.jsp"%>
 		<%@include file="/pid/_painel_outras_atividades.jsp"%>

		
		<rich:panel header="OBSERVAÇÕES GERAIS" id="observacao" styleClass="painelAtividades">
			<rich:column styleClass="esquerda">

			<h:inputTextarea id="observacaoGeral" value="#{cargaHorariaPIDMBean.obj.observacao}" cols="132" rows="10" 
			onkeyup="qteCaracteresObsGeral()" 
			onblur="qteCaracteresObsGeral()"  />
			<br/> <center><i>(5000 caracteres/<span id="txtCaracteresDigitadosGeral">0 digitados</span> )</i></center> 
				
			</rich:column>
		</rich:panel>

			<c:if test="${ not empty cargaHorariaPIDMBean.obj.observacaoChefeDepartamento }">
				<rich:panel header="COMENTÁRIOS FEITOS PEO CHEFE DO DEPARTAMENTO SOBRE O PID" id="observacaoChefe" styleClass="painelAtividades">
				
					<rich:column styleClass="esquerda">
						<h:inputTextarea id="observacaoGeralChefe" value="#{cargaHorariaPIDMBean.obj.observacaoChefeDepartamento}" cols="132" rows="5" 
						disabled="true" />
						<br/> 
					</rich:column>
					
				</rich:panel>
			</c:if>  		
						
		<a4j:outputPanel id="outputResumo">
			<%@include file="/pid/_painel_resumo.jsp"%>   
		</a4j:outputPanel>
		
		<table class="formulario" width="100%" id="tabela">
			<tfoot>
				<tr>
					<td colspan="2" align="center">
						<h:commandButton value="Gravar" action="#{cargaHorariaPIDMBean.cadastrar}" id="cadastrar" />
						<h:commandButton value="Submeter para homologação" action="#{cargaHorariaPIDMBean.paginaConfirmacaoCadastro}" id="enviar" 
						rendered="#{cargaHorariaPIDMBean.obj.cadastrado}" />
						<h:commandButton value="<< Voltar" action="#{cargaHorariaPIDMBean.iniciar}" id="voltar" />
						<h:commandButton value="Cancelar" action="#{cargaHorariaPIDMBean.cancelar}" id="cancelarOperacao" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
		</table>
		
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
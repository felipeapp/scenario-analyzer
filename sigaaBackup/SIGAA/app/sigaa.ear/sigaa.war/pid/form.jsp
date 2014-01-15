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
   	
<h2><ufrn:subSistema /> > Plano Individual do Docente > Formul�rio</h2>

     
	<div class="descricaoOperacao">
		<h5> Caro(a) Professor(a), </h5>
		<p>
			Este formul�rio cont�m os campos necess�rios para o preenchimento do seu Plano Individual Docente, sendo que algumas das informa��es foram populadas previamente a partir dos dados existentes no sistema acad�mico. 
			Ser� solicitado que algumas cargas hor�rias sejam informadas, bem como atividades espec�ficas a serem desenvolvidas no per�odo de refer�ncia deste plano.
		</p>
		<p>
			� poss�vel salvar os dados informados para posterior t�rmino do preenchimento, mas  <strong> para que seja v�lido o PID deve ser submetido � chefia de sua unidade, para que seja realizada a homologa��o</strong>. 
			Ap�s a submiss�o � chefia o plano ainda poder� ser alterado, mas voltar� a ser necess�ria sua homologa��o. 
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

		
		<rich:panel header="OBSERVA��ES GERAIS" id="observacao" styleClass="painelAtividades">
			<rich:column styleClass="esquerda">

			<h:inputTextarea id="observacaoGeral" value="#{cargaHorariaPIDMBean.obj.observacao}" cols="132" rows="10" 
			onkeyup="qteCaracteresObsGeral()" 
			onblur="qteCaracteresObsGeral()"  />
			<br/> <center><i>(5000 caracteres/<span id="txtCaracteresDigitadosGeral">0 digitados</span> )</i></center> 
				
			</rich:column>
		</rich:panel>

			<c:if test="${ not empty cargaHorariaPIDMBean.obj.observacaoChefeDepartamento }">
				<rich:panel header="COMENT�RIOS FEITOS PEO CHEFE DO DEPARTAMENTO SOBRE O PID" id="observacaoChefe" styleClass="painelAtividades">
				
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
						<h:commandButton value="Submeter para homologa��o" action="#{cargaHorariaPIDMBean.paginaConfirmacaoCadastro}" id="enviar" 
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
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<a4j:keepAlive beanName="parecerDocenciaAssistida" />
	<h2> <ufrn:subSistema /> &gt; Plano de Docência Assistida &gt; Registrar Parecer</h2>
	
	<div class="descricaoOperacao">
		<p>
			<b> Caro Usuário, </b>
		</p> 	
		<p>
			Você deverá informar a uma Observação que será anexada ao plano. 
		</p>
	</div>
	
	<table class="visualizacao" style="width: 90%">
		<caption>Dados do Aluno</caption>
		<tr>
			<th>Nome:</th>
			<td>${parecerDocenciaAssistida.plano.discente.matriculaNome}</td>		
		</tr>
		<tr>
			<th>Programa:</th>
			<td>${parecerDocenciaAssistida.plano.discente.unidade.nome}</td>		
		</tr>	
		<tr>
			<th>Orientador:</th>
			<td>${parecerDocenciaAssistida.plano.discente.orientacao.descricaoOrientador}</td>		
		</tr>		
		<tr>
			<th>Nível:</th>
			<td>${parecerDocenciaAssistida.plano.discente.nivelDesc}</td>		
		</tr>
		<tr>
			<th>Ano/Período:</th>
			<td>${parecerDocenciaAssistida.plano.ano}.${parecerDocenciaAssistida.plano.periodo}</td>		
		</tr>
		<tr>
			<th>Situação Atual:</th>
			<td>${parecerDocenciaAssistida.plano.descricaoStatus}</td>		
		</tr>						
	</table>	
	
	<br/>
	
	<h:form id="form">
		<table class="formulario" width="90%">
			<caption>Informe o dado do Parecer</caption>		
			<tr>
				<th class="obrigatorio">
					Observação:
				</th>
				<td>
					<h:inputTextarea cols="80" rows="6" id="observacao" value="#{ parecerDocenciaAssistida.obj.observacao}"/> 
				</td>
			</tr>		
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Confirmar" action="#{parecerDocenciaAssistida.cadastrar}" id="botaoDeconfirmacao"/>
						<h:commandButton value="Cancelar" action="#{parecerDocenciaAssistida.cancelar}" onclick="#{confirm}" immediate="true" id="cancelamento"/>
					</td>
				</tr>
			</tfoot>							
		</table>		
	</h:form>
	
	<center>
		<html:img page="/img/required.gif" style="vertical-align: top;" /> 
		<span class="fontePequena"> Campos de preenchimento obrigatório. </span> 
	</center>	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
	
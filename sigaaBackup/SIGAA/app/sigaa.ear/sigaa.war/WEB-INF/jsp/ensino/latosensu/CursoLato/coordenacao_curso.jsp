<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp" %>

<h2 class="tituloPagina"><ufrn:steps/></h2>

<html:form action="/ensino/latosensu/criarCurso" method="post">

  <html:hidden property="obj.id" />
  <table class="formulario" width="80%">
   <caption>Dados da Coordenação do Curso</caption>
    <tr>
    <td colspan="2">
     <table class=subformulario" width="100%">
	    	<caption>Dados do Coordenador</caption>
			<tbody>
		<tr>
			<th width="30%" class="required">Coordenador:</th>
			<td>
				<html:select property="coordenador.servidor.id">
		       		<html:option value="">-- SELECIONE --</html:option>
		       		<html:options collection="docentesInternos" property="servidor.id" labelProperty="servidor.pessoa.nome"/>
		    	</html:select>
			</td>
		</tr>
		<tr>
			<th class="required">E-mail para Contato:</th>
			<td>
				<html:text property="coordenador.emailContato" maxlength="50" size="50" />
			</td>
		</tr>
		<tr>
			<th class="required">Telefone para Contato:</th>
			<td>
				<html:text property="coordenador.telefoneContato1" maxlength="8" size="15" onkeyup="formatarInteiro(this)" />
			</td>
		</tr>
		<tr>
			<th class="required">Início do Mandato:</th>
			<td>
				<ufrn:calendar property="dataInicioMandatoCoordenador"/>
			</td>
		</tr>
		<tr>
			<th class="required">Fim do Mandato:</th>
			<td>
				<ufrn:calendar property="dataFimMandatoCoordenador"/>
			</td>
		</tr>
		</table>
   	  </td>
	</tr>
	
	<tr>
	<td colspan="2">
		<table class=subformulario" width="100%">
		  <caption>Dados do Vice-Coordenador</caption>
			<tr>
				<th width="30%" class="required">Vice-Coordenador:</th>
				<td>
					<html:select property="viceCoordenador.servidor.id">
			      		<html:option value="">-- SELECIONE --</html:option>
			       		<html:options collection="docentesViceCoordenacao" property="servidor.id" labelProperty="servidor.pessoa.nome"/>
			    	</html:select>
				</td>
			</tr>
			<tr>
				<th class="required">E-mail para Contato:</th>
				<td>
					<html:text property="viceCoordenador.emailContato" maxlength="50" size="50" />
				</td>
			</tr>
			<tr>
				<th class="required">Telefone para Contato:</th>
				<td>
					<html:text property="viceCoordenador.telefoneContato1" maxlength="8" size="15" onkeyup="formatarInteiro(this)" />
				</td>
			</tr>
			<tr>
				<th class="required">Início do Mandato:</th>
				<td>
					<ufrn:calendar property="dataInicioMandatoViceCoordenador"/>
				</td>
			</tr>
			<tr>
				<th class="required">Fim do Mandato:</th>
				<td>
					<ufrn:calendar property="dataFimMandatoViceCoordenador"/>
				</td>
			</tr>
		</table>
	</td>
	</tr>
    
    <tr>
	<td colspan="2">
		<table class=subformulario" width="100%">
			<caption>Dados do Secretário</caption>
		        <tr>
					<th width="30%">Secretário:</th>
					<td>
						<c:set var="tipo" value="todos" />
						<c:set var="idAjax" value="secretario.servidor.id"/>
						<c:set var="nomeAjax" value="secretario.servidor.pessoa.nome"/>
						<%@include file="/WEB-INF/jsp/include/ajax/servidor.jsp" %>
					</td>
				</tr>
				<tr>
					<th>E-mail para Contato:</th>
					<td>
						<html:text property="secretario.emailContato" maxlength="50" size="50" />
					</td>
				</tr>
				<tr>
					<th>Telefone para Contato:</th>
					<td>
						<html:text property="secretario.telefoneContato1" maxlength="8" size="15" onkeyup="formatarInteiro(this)" />
					</td>
				</tr>
		</table>
	  </td>
	</tr>
	
	</tbody>
	<tfoot>
		<tr align="center">
		  <td colspan="10">
			<html:button dispatch="gravar" value="Gravar"/>
			<html:button view="disciplinas" value="<< Voltar"/>
			<html:button dispatch="cancelar" value="Cancelar" cancelar="true"/>
			<html:button dispatch="resumo" value="Avançar >>"/>
		  </td>
		</tr>
	</tfoot>
</table>

<br><br>
<center>
</center>
<br />
<br />
</html:form>

<center>
	<html:img page="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span>
</center>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
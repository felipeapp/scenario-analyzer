<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<%@page import="br.ufrn.sigaa.ensino.dominio.SituacaoMatricula"%>

<style>
	.menu-botoes ul li a h5 {
		left: 50px !important;
	}
	
	.menu-botoes ul li a p {
		background-image: url('/sigaa/img/graduacao/cancelar.png');
		padding-left: 40px !important;
	}
</style>	

<f:view>
	<h2 class="title"><ufrn:subSistema /> > Altera��o do Perfil Inicial </h2>
	
	<div class="descricaoOperacao">
		<p>Caro usu�rio,</p>
		<p> Esta opera��o possibilitar� o acr�scimo do valor de perfil inicial do discente, no qual n�o ser� poss�vel diminu�-lo, 
		ou seja, ficar menor que o perfil inicial calculado atrav�s da contabiliza��o dos aproveitamentos. 
		O novo valor do perfil ser� considerado na estimativa de conclus�o de curso e afins.</p>
		<p> O atual valor de acr�scimo do perfil inicial, caso exista, ser� previamente carregado e listado no campo <i>Valor a ser adicionado no Perfil Inicial sem acr�scimo</i>.</p>
	</div>
	
	<table class="visualizacao" width="80%">
		<tr>
			<th width="20%"> Discente: </th>
			<td> ${discenteGraduacao.obj.discente.matricula } - ${discenteGraduacao.obj.discente.nome }</td>
		</tr>
		<tr>
			<th> Curso: </th>
			<td> ${discenteGraduacao.obj.discente.curso.descricao} </td>
		</tr>
		<tr>
			<th> Ano-Per�odo de Ingresso: </th>
			<td> ${discenteGraduacao.obj.discente.anoPeriodoIngresso} </td>
		</tr>
		
		<ufrn:subSistema teste="graduacao">
			<tr>
				<th> Curr�culo: </th>
				<td> ${discenteGraduacao.obj.discente.curriculo.descricao} </td>
			</tr>
			
			<tr>
				<th> Perfil Inicial sem acr�scimo: </th>
				<td> ${discenteGraduacao.obj.perfilInicial - discenteGraduacao.obj.perfilInicialAlterado} </td>
			</tr>
			
			<tr>
				<th> Perfil Inicial: </th>
				<td> ${discenteGraduacao.obj.perfilInicial} </td>
			</tr>
		</ufrn:subSistema>
	</table>
	<br />

<h:form id="form">
	<table class="formulario" width="65%">
		<caption class="listagem"> Altera��o do Perfil Inicial</caption>
		<tr>
			<th class="obrigatorio" width="60%">Valor a ser adicionado no Perfil Inicial sem acr�scimo: </th>
			<td>
				<h:inputText id="perfilIncialAlterado" value="#{discenteGraduacao.obj.perfilInicialAlterado}" onkeyup="return formatarInteiro(this);" size="4" maxlength="4" style="text-align:right;"/>
			</td>
		</tr>
		<tfoot>
			<tr>
				<td colspan="2"> 
					<h:commandButton value="Confirmar"	action="#{discenteGraduacao.confirmarAlteracaoPerfilInicial}" id="btConfirmar" />
					<h:commandButton value="<< Discentes" action="#{discenteGraduacao.buscaDiscente}" id="btDiscentes" />
					<h:commandButton value="Cancelar" onclick="#{confirm}" immediate="true"	action="#{discenteGraduacao.cancelar}" id="btCancelar" />
				</td>
			</tr>
		</tfoot>
	</table>
	
	<br>
	<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
		class="fontePequena"> Campos de preenchimento obrigat�rio. </span> <br>
	<br>
	</center>

</h:form>	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

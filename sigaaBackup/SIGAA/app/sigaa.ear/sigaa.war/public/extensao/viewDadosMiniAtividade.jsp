<%@include file="/public/include/cabecalho.jsp" %>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<jwr:style src="/public/css/extensao.css" media="all"/>

<f:view>
	<h:messages showDetail="true" />
	<h2>Visualiza��o da Mini Atividade</h2>
	<br>
	
	
	<table class="visualizacao" style="width: 100%;">
		<caption> Dados da Mini Atividade </caption>
		
		<tr>
			<th> T�tulo: </th>
			<td colspan="3"> ${inscricaoParticipanteMiniAtividadeMBean.subAtividadeSelecionada.titulo} </td>
		</tr>
		
		<tr>
			<th> Tipo de Mini Atividade: </th>
			<td  colspan="3"> ${inscricaoParticipanteMiniAtividadeMBean.subAtividadeSelecionada.tipoSubAtividadeExtensao.descricao} </td>
		</tr>
		
		<tr>
			<td colspan="4" style="font-style: italic; text-align: justify;"> ${inscricaoParticipanteMiniAtividadeMBean.subAtividadeSelecionada.descricao} </td>
		</tr>
		
		<tr>
			<th> Local de Realiza��o: </th>
			<td  colspan="3"> ${inscricaoParticipanteMiniAtividadeMBean.subAtividadeSelecionada.local} </td>
		</tr>
		
		<tr>
			<th style="width: 20%;"> Data de In�cio: </th>
			<td style="width: 30%;"> <ufrn:format type="data" valor="${inscricaoParticipanteMiniAtividadeMBean.subAtividadeSelecionada.inicio}" />  </td>
			<th style="width: 20%;"> Data de T�rmino: </th>
			<td style="width: 30%;"> <ufrn:format type="data" valor="${inscricaoParticipanteMiniAtividadeMBean.subAtividadeSelecionada.fim}" /> </td>
		</tr>
		
		<tr>
			<th> Hor�rio: </th>
			<td> ${inscricaoParticipanteMiniAtividadeMBean.subAtividadeSelecionada.horario} </td>
			<th> Carga Hor�ria: </th>
			<td> ${inscricaoParticipanteMiniAtividadeMBean.subAtividadeSelecionada.cargaHoraria} </td>
		</tr>
		
		
		<tr>
			<th> N�mero de Vagas: </th>
			<td colspan="3"> ${inscricaoParticipanteMiniAtividadeMBean.subAtividadeSelecionada.numeroVagas} </td>
		</tr>
		
		<tr>
			<td colspan="4" class="subFormulario"> Dados da Atividade: </td>
		</tr>
		
		<tr>
			<th> Atividade: </th>
			<td colspan="3"> ${inscricaoParticipanteMiniAtividadeMBean.subAtividadeSelecionada.atividade.projeto.titulo} </td>
		</tr>
		
		<tr>
			<th> Tipo: </th>
			<td> ${inscricaoParticipanteMiniAtividadeMBean.subAtividadeSelecionada.atividade.tipoAtividadeExtensao.descricao} </td>
			<th> Ano: </th>
			<td> ${inscricaoParticipanteMiniAtividadeMBean.subAtividadeSelecionada.atividade.projeto.ano} </td>
		</tr>
		
		<tr>
			<th> Coordenador: </th>
			<td colspan="3"> ${inscricaoParticipanteMiniAtividadeMBean.subAtividadeSelecionada.atividade.projeto.coordenador.pessoa.nome} </td>
		</tr>
		
	</table>
	
	
	
	<br />
		<div style="margin: 0pt auto; width: 80%; text-align: center;">
			<a href="javascript:history.go(-1)">&lt;&lt; voltar</a>
		</div>
	<br />
	
</f:view>

<%@include file="/public/include/rodape.jsp" %>
<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style>
<!--
	h2{
		padding-top: 2cm;
		padding-bottom: 2cm;
		font-size: 1.5em;
		text-align: center;
		letter-spacing: 0.4em;
		word-spacing: 0.4em;
	}
	
	#divAutenticacao {
		width: 97%;
		margin: 10px auto 2px;
		text-align: center;
	}
	
	#divAutenticacao h4 {
		border-bottom: 1px solid #BBB;
		margin-bottom: 3px;
		padding-bottom: 2px;
	}
	
	#divAutenticacao span {
		color: #922;
		font-weight: bold;
	}	
	
	#div1{
		font-weight: bold;
		color: red;
		width: 100%;
		text-align: center;
	}
	
	#div2{
		margin-top:  20px;  
		font-style: italic;
		width: 100%;
		text-align: center;
	}		
-->
</style>


<c:if test="${requestScope.liberaEmissao == true}"> <%-- seguran�a importante senao o usuario pode acessar a pagina diretamente e ver o documento --%>
	<h2 style="border-bottom: 0;">
		DECLARA��O
	</h2>
	
	<p align="center" style="text-align: justify; font-size: 1.3em; line-height: 1.7em; text-indent: 40px;">
	Declaramos, para os devidos fins, que o prazo m�ximo para integraliza��o curricular do Curso de 
	<b>${declaracaoPrazoMaximoMBean.obj.matriz.curso.nome}</b> - Turno <b>${declaracaoPrazoMaximoMBean.obj.matriz.turno.descricao}</b> - 
	Modalidade <b>${declaracaoPrazoMaximoMBean.obj.matriz.grauAcademico.descricao}</b> � de <b>${declaracaoPrazoMaximoMBean.obj.semestreConclusaoMaximo} 
	per�odos letivos regulares</b>.
	</p>
	<p align="center" style="text-align: justify; font-size: 1.3em; line-height: 1.7em; text-indent: 40px;padding-bottom: 2cm;">
	${declaracaoPrazoMaximoMBean.descricaoDepartamento} da ${declaracaoPrazoMaximoMBean.descricaoReitoriaGraduacao} da ${ configSistema['nomeInstituicao'] }, 
	em ${ configSistema['cidadeInstituicao'] }, <ufrn:format type="dia_mes_ano" valor="${declaracaoPrazoMaximoMBean.comprovante.dataEmissao}" />.
	</p>
	
	
	<div id="divAutenticacao">
		<h4>ATEN��O</h4>
		<p>
			Para verificar a autenticidade deste documento acesse
			<span>${ configSistema['linkSigaa'] }/sigaa/documentos/</span> informando o identificador <span>${declaracaoPrazoMaximoMBean.sementeDocumento}</span>, a data de emiss�o e
			o c�digo de verifica��o <span>${declaracaoPrazoMaximoMBean.codigoSeguranca}</span>
		</p>
	</div>

</c:if>
<c:if test="${!requestScope.liberaEmissao}"> <%-- seguran�a importante senao o usuario pode acessar a pagina diretamente e ver o documento --%>	
		<div style="margin-bottom:30px;">	
			<div id="div1"> ERRO NA GERA��O DA DECLARA��O DE PRAZO M�XIMO DE INTEGRALIZA��O DE CURR�CULO.</div>
	
			<div id="div2"> As informa��es do documento s�o inv�lidas </div>
		</div>	
</c:if>	
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
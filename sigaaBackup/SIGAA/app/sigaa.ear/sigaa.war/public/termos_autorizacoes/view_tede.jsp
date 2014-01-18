<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style>

#divMensagemDeclaracao{
	margin-bottom:30px;
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

#divBloquearUsuario{
	text-align: center;
	margin-bottom: 20px;
}
</style>

	<div align="center">
	<h3>Termo de Autoriza��o para Publica��o de Teses e Disserta��es
	Eletr�nicas (TOE) na Biblioteca Digital de Teses e Disserta��es (BDTD)</h3>
	</div>
	<c:if test="${requestScope.liberaEmissao == true}"> <%-- seguran�a importante senao o usuario pode acessar a pagina diretamente e ver o documento --%> 		
		<br />
		${termoPublicacaoTD.textoEmissaoTEDE}
		<br />
		<b>1. Identifica��o do material bibliogr�fico</b>
		<ul>
			<li>${termoPublicacaoTD.tipoDocumento}</li>
		</ul>
		<b>2. Identifica��o da ${termoPublicacaoTD.tipoDocumento}</b>
		<br />
		<br />
		<table class="listagem">
			<tr>
				<th>Autor:</th>
				<td colspan="3">${termoPublicacaoTD.obj.discente.nome}</td>
				<th>Matr�cula:</th>
				<td>${termoPublicacaoTD.obj.discente.matricula} <i>( identificador )</i></td>					
			</tr>
			<tr>
				<th>Telefone:</th>
				<td colspan="5">${termoPublicacaoTD.obj.discente.pessoa.telefone}</td>
			</tr>			
			<tr>
				<th>Identidade:</th>
				<td>${termoPublicacaoTD.obj.discente.pessoa.identidade.numero } - 
				${termoPublicacaoTD.obj.discente.pessoa.identidade.orgaoExpedicao }/${termoPublicacaoTD.obj.discente.pessoa.unidadeFederativa.sigla }</td>
				<th>CPF:</th>
				<td></td>
				<th>E-mail:</th>
				<td>${termoPublicacaoTD.obj.discente.pessoa.email}</td>
			</tr>
			<tr>
				<th>Orientador:</th>
				<td>${termoPublicacaoTD.obj.discente.orientacao.nomeOrientador}
					<c:if test="${empty termoPublicacaoTD.obj.discente.orientacao.nomeOrientador}">
						<span style="color: red;">N�O INFORMADO</span>
					</c:if>
				</td>
				<th>CPF:</th>
				<td><ufrn:format type="cpf_cnpj"
					valor="${termoPublicacaoTD.obj.discente.orientacao.pessoa.cpf_cnpj}" /></td>
				<th>E-mail:</th>
				<td>${termoPublicacaoTD.obj.discente.orientacao.pessoa.email}</td>
			</tr>
			<c:forEach items="${termoPublicacaoTD.obj.banca.membrosBanca}" var="membro">
				<tr>
					<th>Membro da banca:</th>
					<td>${membro.nome}</td>
					<th>CPF:</th>
					<td><ufrn:format type="cpf_cnpj" valor="${membro.pessoa.cpf_cnpj}" /></td>
					<th>E-mail:</th>
					<td>${membro.pessoa.email}</td>
				</tr>
			</c:forEach>
			<tr>
				<th>Data da Defesa:</th>
				<td colspan="2">
					<ufrn:format type="data" valor="${termoPublicacaoTD.obj.banca.data}"/>
				</td>
				<th>Titula��o:</th>
				<td colspan="2">${termoPublicacaoTD.titulacao}</td>
			</tr>
			<tr>
				<th>T�tulo:</th>
				<td colspan="5">${termoPublicacaoTD.obj.banca.dadosDefesa.titulo}</td>
			</tr>
			<tr>
				<th>Institui��o de Defesa:</th>
				<td colspan="3">${ configSistema['nomeInstituicao']}/${ configSistema['siglaInstituicao']}</td>
				<th>CNPJ:</th>
				<td>${ configSistema['cnpjInstituicao']}</td>
			</tr>
			<tr>
				<th>Afilia��o:</th>
				<td colspan="3">${termoPublicacaoTD.obj.afiliacao}</td>
				<th>CNPJ:</th>
				<td><c:if test="${termoPublicacaoTD.obj.CNPJAfiliacao > 0}">
					<ufrn:format type="cpf_cnpj" valor="${termoPublicacaoTD.obj.CNPJAfiliacao}" />
				</c:if></td>
			</tr>
			<tr>
				<th>Palavras-chave:</th>
				<td colspan="5">${termoPublicacaoTD.obj.banca.dadosDefesa.palavrasChave}</td>
			</tr>
		</table>
		<br />
		<b>3. Ag�ncia de Fomento</b>
		<ul>
			<LI>${termoPublicacaoTD.obj.instituicaoFomento.nome}</LI>
			<LI>
				<c:if test="${empty termoPublicacaoTD.obj.instituicaoFomento.nome}">
					<span style="color: red;">N�O INFORMADO</span>
				</c:if>
		</ul>
		<b>4. Informa��o de acesso ao documento</b>
		<ul>
			<li>Libera��o para publica��o: 
				<b>
					<c:if test="${termoPublicacaoTD.obj.parcial}">
						Parcial
					</c:if>
					<c:if test="${!termoPublicacaoTD.obj.parcial}">
						Total
					</c:if>
				</b>	
			</li>	
			<c:if test="${not empty termoPublicacaoTD.obj.restricoes}">
				<li>
					Restri��es � publica��o: ${termoPublicacaoTD.obj.restricoes}
				</li>
			</c:if>		
		</ul>
		<br />
		<table align="center">
			<tr>
				<td align="center">___________________________________________</td>
				<td width="100"></td>
				<td align="center">________________________________</td>
			</tr>
			<tr>
				<td align="center">Assinatura do autor</td>
				<td></td>
				<td align="center">Data</td>
			</tr>
		</table>
		<br />
		<br />
		
		<p align="center"><b>Havendo concord�ncia com a publica��o
		eletr�nica, toma-se imprescind�vel o envio em formato digital da tese
		ou disserta��o.</b></p>
		<br />
		<div id="divAutenticacao">
			<h4 align="center">ATEN��O</h4>
			<p>
				Para verificar a autenticidade deste documento acesse
				<span>${ configSistema['linkSigaa'] }/sigaa/documentos/</span> informando o identificador, a data de emiss�o e
				o c�digo de verifica��o <span>${termoPublicacaoTD.codigoSeguranca}</span>
			</p>
		</div>	
		<br/>		
		<table align="center" class="listagem">
			<tr>
				<td> 
					<img src="/sigaa/img/biblioteca/tede/bczm.gif"/>
				</td>
				<td> 
					<img src="/sigaa/img/biblioteca/tede/logo_ibict.jpg"/>
				</td>
				<td> 
					<img src="/sigaa/img/biblioteca/tede/logo_tede.jpg"/>
				</td>
				<td> 
					<img src="/sigaa/img/biblioteca/tede/logo_bdtd.jpg"/>
				</td>				
			</tr>
		</table>		
	
	</c:if>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
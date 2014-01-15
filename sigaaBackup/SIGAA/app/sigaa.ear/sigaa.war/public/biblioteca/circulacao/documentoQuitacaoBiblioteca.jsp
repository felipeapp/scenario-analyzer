

 <%-- JSP que representa o documento de quitacao da biblioteca --%>

<%@ include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<%@ taglib uri="/tags/rich" prefix="rich"%>
<%@ taglib uri="/tags/a4j" prefix="a4j"%>

<style type="text/css">

h4{
	text-align: center;
}

h3{
	margin-top:20px;
	margin-bottom:30px;
	text-align: center;
}

#divDadosUsuario {
	margin-bottom:30px;
}

#divDadosUsuario span{
	font-weight: bold;
}


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

	
	
<c:if test="${requestScope.liberaEmissao == true}"> <%-- segurança importante senao o usuario pode acessar a pagina diretamente e ver o documento --%> 


	<h4>${verificaSituacaoUsuarioBibliotecaMBean.descricaoSistemaBiblioteca}</h4>
	
	<h3>DECLARAÇÃO DE QUITAÇÃO </h3>
	

	<div id="divDadosUsuario">
	
			
			<c:if test="${verificaSituacaoUsuarioBibliotecaMBean.informacoesEmprestimosPorVinculoEscolhido.infoUsuarioBiblioteca.vinculo.vinculoServidor}">
				<span>SIAPE:</span> <c:out  value="${verificaSituacaoUsuarioBibliotecaMBean.informacoesEmprestimosPorVinculoEscolhido.infoUsuarioBiblioteca.siape}"/> <i> ( identificador ) </i> <br/>
			</c:if>
	
			<c:if test="${verificaSituacaoUsuarioBibliotecaMBean.informacoesEmprestimosPorVinculoEscolhido.infoUsuarioBiblioteca.vinculo.vinculoAluno}">
				<span>MATRÍCULA:</span> <c:out  value="${verificaSituacaoUsuarioBibliotecaMBean.informacoesEmprestimosPorVinculoEscolhido.infoUsuarioBiblioteca.matricula}"/> <i> ( identificador ) </i> <br/>
			</c:if>
		
			<c:if test="${verificaSituacaoUsuarioBibliotecaMBean.informacoesEmprestimosPorVinculoEscolhido.infoUsuarioBiblioteca.vinculo.vinculoBiblioteca}">
				<span>CÓDIGO DA BIBLIOTECA:</span> ${verificaSituacaoUsuarioBibliotecaMBean.informacoesEmprestimosPorVinculoEscolhido.infoUsuarioBiblioteca.codigoBiblioteca} <i> ( identificador ) </i><br/>
			</c:if>
			
			<c:if test="${! verificaSituacaoUsuarioBibliotecaMBean.informacoesEmprestimosPorVinculoEscolhido.infoUsuarioBiblioteca.vinculo.vinculoServidor 
				&&  ! verificaSituacaoUsuarioBibliotecaMBean.informacoesEmprestimosPorVinculoEscolhido.infoUsuarioBiblioteca.vinculo.vinculoAluno
			 	&& ! verificaSituacaoUsuarioBibliotecaMBean.informacoesEmprestimosPorVinculoEscolhido.infoUsuarioBiblioteca.vinculo.vinculoBiblioteca}">
				
				<c:if test="${verificaSituacaoUsuarioBibliotecaMBean.informacoesEmprestimosPorVinculoEscolhido.infoUsuarioBiblioteca.contemCPF}">
					<span>CPF:</span> <c:out  value="${verificaSituacaoUsuarioBibliotecaMBean.informacoesEmprestimosPorVinculoEscolhido.infoUsuarioBiblioteca.CPF}"/> <i> ( identificador )  </i> <br/>
				</c:if>
				<c:if test="${! verificaSituacaoUsuarioBibliotecaMBean.informacoesEmprestimosPorVinculoEscolhido.infoUsuarioBiblioteca.contemCPF}">
					<span>Passaporte:</span> <c:out  value="${verificaSituacaoUsuarioBibliotecaMBean.informacoesEmprestimosPorVinculoEscolhido.infoUsuarioBiblioteca.passaporte}"/> <i> ( identificador )  </i> <br/>
				</c:if>
			</c:if>
	
	
	
	
	
			<c:if test="${! verificaSituacaoUsuarioBibliotecaMBean.informacoesEmprestimosPorVinculoEscolhido.infoUsuarioBiblioteca.vinculo.vinculoBiblioteca}">
				<span>USUÁRIO:</span> Sr(a). <c:out  value="${verificaSituacaoUsuarioBibliotecaMBean.informacoesEmprestimosPorVinculoEscolhido.infoUsuarioBiblioteca.nomeUsuario}"/> <br/>
			</c:if>
			
			<c:if test="${verificaSituacaoUsuarioBibliotecaMBean.informacoesEmprestimosPorVinculoEscolhido.infoUsuarioBiblioteca.vinculo.vinculoBiblioteca}">
				<span>DESCRIÇÃO:</span>  <c:out  value="${verificaSituacaoUsuarioBibliotecaMBean.informacoesEmprestimosPorVinculoEscolhido.infoUsuarioBiblioteca.nomeUsuario}"/> <br/>
			</c:if>
			
			
	
			
			<span>VÍNCULO DO USUÁRIO:</span> <c:out value="${verificaSituacaoUsuarioBibliotecaMBean.informacoesEmprestimosPorVinculoEscolhido.infoUsuarioBiblioteca.vinculo.descricao}"/>	<br/>
	
	
	
			
			<c:if test="${verificaSituacaoUsuarioBibliotecaMBean.informacoesEmprestimosPorVinculoEscolhido.infoUsuarioBiblioteca.vinculo.vinculoServidor}">
				
				<span>LOTAÇÃO: </span>   <c:out value="${verificaSituacaoUsuarioBibliotecaMBean.informacoesEmprestimosPorVinculoEscolhido.infoUsuarioBiblioteca.lotacao}" /> <br/>
				<span>CARGO: </span> <c:out value="${verificaSituacaoUsuarioBibliotecaMBean.informacoesEmprestimosPorVinculoEscolhido.infoUsuarioBiblioteca.cargo}" /> <br/>
												
			</c:if>
	
			
			<c:if test="${verificaSituacaoUsuarioBibliotecaMBean.informacoesEmprestimosPorVinculoEscolhido.infoUsuarioBiblioteca.vinculo.vinculoAluno}">
			
				<span>CENTRO: </span><c:out value="${verificaSituacaoUsuarioBibliotecaMBean.informacoesEmprestimosPorVinculoEscolhido.infoUsuarioBiblioteca.centro}" /> <br/>
				<span>CURSO: </span><c:out value="${verificaSituacaoUsuarioBibliotecaMBean.informacoesEmprestimosPorVinculoEscolhido.infoUsuarioBiblioteca.curso}" /> <br/>
			</c:if>
			
	
		
		
	</div> 
	
	
	
	<div id="divMensagemDeclaracao">
		
		<c:if test="${! verificaSituacaoUsuarioBibliotecaMBean.usuarioNaoPossuiNenhumVinculo}">
			&nbsp&nbsp&nbsp&nbsp Declaramos, para os devidos fins, que em nome do usuário(a) supracitado(a), não existe débitos
			 nas bibliotecas da ${ configSistema['siglaInstituicao']} feitos com o vínculo ${verificaSituacaoUsuarioBibliotecaMBean.informacoesEmprestimosPorVinculoEscolhido.infoUsuarioBiblioteca.vinculo.descricao}
			 acima mostrado.
			 <br/><br/>
			&nbsp&nbsp&nbsp&nbsp Esse vínculo foi quitado e não poderá mais ser usado para realizar empréstimos.
	 	</c:if>
	 	
	 	<c:if test="${verificaSituacaoUsuarioBibliotecaMBean.usuarioNaoPossuiNenhumVinculo}">
	 	
		 	<div style="text-align: center; font-weight: bold; ">
				<i> <span> Usuário nunca possuiu cadastro no sistema de biblioteca. </span>  </i>
			</div>
		 	
		 	<br/><br/>
			 &nbsp&nbsp&nbsp&nbsp Declaramos, para os devidos fins, que em nome do usuário(a) supracitado(a), não existe débitos
			 nas bibliotecas da ${ configSistema['siglaInstituicao']}.
		
	 	</c:if>
	 	
	</div>	


	
				
	<div id="divAutenticacao">
		<h4>ATENÇÃO</h4>
		<p>
			Para verificar a autenticidade deste documento acesse
			<span>${ configSistema['linkSigaa'] }/sigaa/documentos/</span> informando o identificador, a data de emissão e
			o código de verificação <span>${verificaSituacaoUsuarioBibliotecaMBean.codigoSeguranca}</span>
		</p>
	</div>


	




<c:if test="${requestScope.exibindo_area_publica == true}">
	<%@ include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
</c:if>


<c:if test="${requestScope.exibindo_area_publica != true}">
	<%@page import="br.ufrn.arq.util.AmbienteUtils"%>

		</div> <%-- Fim do div relatorio  --%>
		<div class="clear"> </div>
		<br/>
		<div id="relatorio-rodape">
			<p>
				<table width="100%">
					<tr>
						<f:view>
							<%-- Mantém as informações do usuário e a situação entre as requisições --%>
							<a4j:keepAlive beanName="verificaSituacaoUsuarioBibliotecaMBean"></a4j:keepAlive>
							<a4j:keepAlive beanName="buscaUsuarioBibliotecaMBean" />
							
							<h:form>
							<td class="voltar" align="left"> <h:commandLink id="voltarDocumentoQuitacao" value="Voltar" action="#{verificaSituacaoUsuarioBibliotecaMBean.atualizaInformacoesVinculosDiretoPagina}"> </h:commandLink> </td>
							</h:form>
						</f:view>
						
						<td width="70%"  align="center">${ configSistema['siglaSigaa']} | Copyright &copy; <%= br.ufrn.arq.util.UFRNUtils.getCopyright(2006) %> - ${configSistema['nomeResponsavelInformatica']} - ${configSistema['siglaInstituicao']} - ${ configSistema['telefoneHelpDesk'] } - <%= AmbienteUtils.getNomeServidorComInstancia() %></td>
						
						<td class="naoImprimir" align="right">
							<a onclick="javascript:window.print();" href="#">Imprimir</a>
						</td>
						
						<td class="naoImprimir" align="right">
							<a onclick="javascript:window.print();" href="#">							
								<img alt="Imprimir" title="Imprimir" src="/shared/javascript/ext-1.1/docs/resources/print.gif"/>
							</a>
						</td>
					</tr>
				</table>
			</p>
	
		</div>
	</div>  <%-- Fim do div 'container' --%>
	
	
	</body>
	</html>

</c:if>


</c:if> <%-- Se está liberado a emissão, o usuário não acessou a página diretamente --%>





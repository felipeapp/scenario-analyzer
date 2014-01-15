<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<f:view>

	<h2 style="text-align: center;">Termo de Responsabilidade</h2>
	<p style="line-height:200%; padding-left:circulacao 30px; font-weight: bold; text-align: justify;">&nbsp;&nbsp;&nbsp;&nbsp;${emiteTermoAdesaoBibliotecaMBean.termoDeAdesaoSelecionado.texto}	</p>
	<br>
	
	<p style="line-height:200%; padding-left: 30px;"> CPF/Passaporte: ${emiteTermoAdesaoBibliotecaMBean.termoDeAdesaoSelecionado.cpfPassaporte} </p>
	<c:if test="${emiteTermoAdesaoBibliotecaMBean.termoDeAdesaoSelecionado.matricula != null}">
		<p style="line-height:200%; padding-left: 30px;"> Matrícula: ${emiteTermoAdesaoBibliotecaMBean.termoDeAdesaoSelecionado.matricula} </p>
	</c:if> 
	
	
	<p style="line-height:200%; padding-left: 30px;"> Nome: ${emiteTermoAdesaoBibliotecaMBean.termoDeAdesaoSelecionado.nomePessoa}</p>
	
	<p style="line-height:200%; padding-left: 30px;"> 
		<c:if test="${ not empty emiteTermoAdesaoBibliotecaMBean.termoDeAdesaoSelecionado.nomeUnidade}"> 
			Unidade: ${emiteTermoAdesaoBibliotecaMBean.termoDeAdesaoSelecionado.nomeUnidade} 
		</c:if>
	</p>
	
	<br>
	<p style="line-height:200%; padding-left: 30px;"> Data da Assinatura: ${emiteTermoAdesaoBibliotecaMBean.termoDeAdesaoSelecionado.dataFormatada} </p>
	<br>
	
	<p style="line-height:200%; padding-left: 30px;color: grey; text-align: center;"> Hash: ${emiteTermoAdesaoBibliotecaMBean.termoDeAdesaoSelecionado.hashMd5} </p>
	
	<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
	
</f:view>
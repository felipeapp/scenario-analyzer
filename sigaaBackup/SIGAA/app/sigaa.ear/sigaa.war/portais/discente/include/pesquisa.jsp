<%@ taglib uri="/tags/struts-html" prefix="html"  %>
<%@ taglib uri="/tags/ufrn" prefix="ufrn" %>
    
    <ul>
    	<li> <html:link action="/pesquisa/projetoPesquisa/buscarProjetos.do?dispatch=consulta&popular=true">Consultar Projetos</html:link> </li>
	</ul>
	<c:if test="${usuario.discenteAtivo.graduacao or usuario.discenteAtivo.tecnico}">
		<ul>	
			<li> Plano Trabalho
	            <ul>
	    			<li> <html:link action="/pesquisa/planoTrabalho/wizard.do?dispatch=listarPorDiscente">Meus Planos de Trabalho</html:link> </li>
	            </ul>
			</li>
		</ul>
		<ul>
			<li> Relatórios de Iniciação Científica Parcial
	            <ul>
	    			<li> <html:link action="/pesquisa/relatorioBolsaParcial.do?dispatch=listarPlanos">Enviar</html:link> </li>
	    			<li> <html:link action="/pesquisa/relatorioBolsaParcial.do?dispatch=listarRelatorios">Consultar</html:link> </li>
	            </ul>
			</li>
		</ul>
		<ul>
			<li> Relatórios Finais
	            <ul>
	    			<li> <html:link action="/pesquisa/relatorioBolsaFinal.do?dispatch=listarPlanos">Enviar</html:link> </li>
	    			<li> <html:link action="/pesquisa/relatorioBolsaFinal.do?dispatch=listarRelatorios">Consultar</html:link> </li>
	            </ul>
			</li>
		</ul>
	 </c:if>
		<ul>
			<li> Congresso de Iniciação Científica
	            <ul>
	    			<c:if test="${usuario.discenteAtivo.graduacao}">
	    				<li> <html:link action="/pesquisa/resumoCongresso.do?dispatch=popularInicioEnvio">Submeter resumo</html:link> </li>
	    			</c:if>
	    			<li> <html:link action="/pesquisa/resumoCongresso.do?dispatch=listarResumosAutor">Meus resumos</html:link> </li>
	            </ul>
			</li>
	    </ul>